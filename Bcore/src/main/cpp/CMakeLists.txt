cmake_minimum_required(VERSION 3.22.1)
project("BoxCore" CXX C)

set(CMAKE_CXX_STANDARD 17)
set(CMAKE_CXX_STANDARD_REQUIRED ON)

if (CMAKE_ANDROID_ARCH_ABI STREQUAL "arm64-v8a")
    set(ARCH_INCLUDE_PATH "/data/data/com.itsaky.androidide/files/usr/include/aarch64-linux-android")
elseif (CMAKE_ANDROID_ARCH_ABI STREQUAL "armeabi-v7a")
    set(ARCH_INCLUDE_PATH "/data/data/com.itsaky.androidide/files/usr/include/arm-linux-androideabi")
endif()

include_directories(SYSTEM /data/data/com.itsaky.androidide/files/usr/include)
include_directories(SYSTEM /data/data/com.itsaky.androidide/files/usr/include/c++/v1)
include_directories(SYSTEM ${ARCH_INCLUDE_PATH})

add_compile_options(-fvisibility=hidden -ffunction-sections -fdata-sections)
add_link_options(-Wl,--gc-sections -Wl,--exclude-libs,ALL)

add_library(xdl STATIC
    xdl/xdl.c
    xdl/xdl_iterate.c
    xdl/xdl_linker.c
    xdl/xdl_lzma.c
    xdl/xdl_util.c
)
target_include_directories(xdl PUBLIC ${CMAKE_CURRENT_SOURCE_DIR})

add_library(dobby STATIC IMPORTED)
if (CMAKE_ANDROID_ARCH_ABI STREQUAL "arm64-v8a")
    set_target_properties(dobby PROPERTIES IMPORTED_LOCATION ${CMAKE_CURRENT_SOURCE_DIR}/Dobby/arm64-v8a/libdobby.a)
elseif (CMAKE_ANDROID_ARCH_ABI STREQUAL "armeabi-v7a")
    set_target_properties(dobby PROPERTIES IMPORTED_LOCATION ${CMAKE_CURRENT_SOURCE_DIR}/Dobby/armeabi-v7a/libdobby.a)
endif()

file(GLOB_RECURSE HOOK_SOURCES "Hook/*.cpp" "JniHook/*.cpp" "Utils/*.cpp")
add_library(BoxCore SHARED BoxCore.cpp IO.cpp hidden_api.cpp ${HOOK_SOURCES})

target_include_directories(BoxCore PRIVATE
    ${CMAKE_CURRENT_SOURCE_DIR}
    ${CMAKE_CURRENT_SOURCE_DIR}/Hook
    ${CMAKE_CURRENT_SOURCE_DIR}/JniHook
    ${CMAKE_CURRENT_SOURCE_DIR}/Utils
    ${CMAKE_CURRENT_SOURCE_DIR}/xdl
)

target_link_libraries(BoxCore log android xdl dobby)
