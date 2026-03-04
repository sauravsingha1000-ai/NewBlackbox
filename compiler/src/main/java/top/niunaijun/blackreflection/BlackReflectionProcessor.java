package top.niunaijun.blackreflection;

import com.google.auto.service.AutoService;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeSpec;

import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Filer;
import javax.annotation.processing.Messager;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;
import javax.tools.Diagnostic;

/**
 * Annotation processor that generates concrete implementation classes for
 * interfaces annotated with {@code @BClass}.
 *
 * <p>For each annotated interface it produces a {@code <InterfaceName>Impl} class
 * in the same package that uses Java reflection to access hidden Android APIs.
 */
@AutoService(Processor.class)
public class BlackReflectionProcessor extends AbstractProcessor {

    private Filer mFiler;
    private Messager mMessager;

    @Override
    public synchronized void init(ProcessingEnvironment processingEnv) {
        super.init(processingEnv);
        mFiler = processingEnv.getFiler();
        mMessager = processingEnv.getMessager();
    }

    @Override
    public Set<String> getSupportedAnnotationTypes() {
        Set<String> types = new LinkedHashSet<>();
        types.add("top.niunaijun.blackreflection.annotation.BClass");
        return types;
    }

    @Override
    public SourceVersion getSupportedSourceVersion() {
        return SourceVersion.latestSupported();
    }

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        for (Element element : roundEnv.getElementsAnnotatedWith(
                processingEnv.getElementUtils().getTypeElement(
                        "top.niunaijun.blackreflection.annotation.BClass"))) {

            if (element.getKind() != ElementKind.INTERFACE) {
                mMessager.printMessage(Diagnostic.Kind.ERROR,
                        "@BClass can only be applied to interfaces", element);
                continue;
            }

            TypeElement interfaceElement = (TypeElement) element;
            generateImplementation(interfaceElement);
        }
        return true;
    }

    private void generateImplementation(TypeElement interfaceElement) {
        String packageName = processingEnv.getElementUtils()
                .getPackageOf(interfaceElement).getQualifiedName().toString();
        String interfaceName = interfaceElement.getSimpleName().toString();
        String implName = interfaceName + "Impl";

        TypeSpec.Builder classBuilder = TypeSpec.classBuilder(implName)
                .addModifiers(Modifier.PUBLIC, Modifier.FINAL)
                .addSuperinterface(ClassName.get(interfaceElement))
                .addField(Object.class, "mReceiver", Modifier.PRIVATE, Modifier.FINAL)
                .addField(Class.class, "mClass", Modifier.PRIVATE, Modifier.FINAL);

        // Constructor
        MethodSpec constructor = MethodSpec.constructorBuilder()
                .addModifiers(Modifier.PUBLIC)
                .addParameter(Object.class, "receiver")
                .addParameter(Class.class, "clz")
                .addStatement("this.mReceiver = receiver")
                .addStatement("this.mClass = clz")
                .build();
        classBuilder.addMethod(constructor);

        // Implement each interface method
        for (Element enclosed : interfaceElement.getEnclosedElements()) {
            if (enclosed.getKind() != ElementKind.METHOD) continue;
            ExecutableElement method = (ExecutableElement) enclosed;
            classBuilder.addMethod(buildMethodImpl(method));
        }

        TypeSpec typeSpec = classBuilder.build();
        JavaFile javaFile = JavaFile.builder(packageName, typeSpec).build();

        try {
            javaFile.writeTo(mFiler);
        } catch (IOException e) {
            mMessager.printMessage(Diagnostic.Kind.ERROR,
                    "Failed to write " + implName + ": " + e.getMessage());
        }
    }

    private MethodSpec buildMethodImpl(ExecutableElement method) {
        MethodSpec.Builder builder = MethodSpec.overriding(method);

        TypeMirror returnType = method.getReturnType();
        List<? extends VariableElement> params = method.getParameters();

        // Build param types array
        StringBuilder paramTypeArray = new StringBuilder("new Class<?>[]{");
        StringBuilder paramArray = new StringBuilder("new Object[]{");
        for (int i = 0; i < params.size(); i++) {
            if (i > 0) {
                paramTypeArray.append(", ");
                paramArray.append(", ");
            }
            paramTypeArray.append(params.get(i).getSimpleName()).append(".getClass()");
            paramArray.append(params.get(i).getSimpleName());
        }
        paramTypeArray.append("}");
        paramArray.append("}");

        builder.addStatement("$T method = null", Method.class);
        builder.beginControlFlow("try");
        builder.addStatement("method = mClass.getDeclaredMethod($S, $L)",
                method.getSimpleName().toString(), paramTypeArray.toString());
        builder.addStatement("method.setAccessible(true)");

        if (returnType.getKind() == TypeKind.VOID) {
            builder.addStatement("method.invoke(mReceiver, $L)", paramArray.toString());
        } else {
            builder.addStatement("return ($T) method.invoke(mReceiver, $L)",
                    ClassName.bestGuess(returnType.toString()), paramArray.toString());
        }
        builder.nextControlFlow("catch ($T e)", Exception.class);
        builder.addStatement("android.util.Log.e($S, $S, e)",
                "BlackReflection", "invoke failed: " + method.getSimpleName());
        builder.endControlFlow();

        if (returnType.getKind() != TypeKind.VOID) {
            if (returnType.getKind().isPrimitive()) {
                builder.addStatement("return 0"); // primitive default
            } else {
                builder.addStatement("return null");
            }
        }
        return builder.build();
    }
}
