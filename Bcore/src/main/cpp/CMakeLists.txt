cmake_minimum_required(VERSION 3.22.1)
project("BoxCore" C CXX)

set(CMAKE_CXX_STANDARD 17)
set(CMAKE_CXX_STANDARD_REQUIRED ON)

# Compiler optimizations
add_compile_options(
        -fvisibility=hidden
        -ffunction-sections
        -fdata-sections
)

# Linker optimizations
add_link_options(
        -Wl,--gc-sections
        -Wl,--exclude-libs,ALL
)

# -------------------------
# xdl static library
# -------------------------
add_library(xdl STATIC
        xdl/xdl.c
        xdl/xdl_iterate.c
        xdl/xdl_linker.c
        xdl/xdl_lzma.c
        xdl/xdl_util.c
)

target_include_directories(xdl PUBLIC
        ${CMAKE_CURRENT_SOURCE_DIR}
)

# -------------------------
# dobby hook library
# -------------------------
add_library(dobby STATIC IMPORTED)

if (CMAKE_ANDROID_ARCH_ABI STREQUAL "arm64-v8a")
    set_target_properties(dobby PROPERTIES
            IMPORTED_LOCATION
            ${CMAKE_CURRENT_SOURCE_DIR}/Dobby/arm64-v8a/libdobby.a)
elseif (CMAKE_ANDROID_ARCH_ABI STREQUAL "armeabi-v7a")
    set_target_properties(dobby PROPERTIES
            IMPORTED_LOCATION
            ${CMAKE_CURRENT_SOURCE_DIR}/Dobby/armeabi-v7a/libdobby.a)
endif()

# -------------------------
# Source files
# -------------------------
file(GLOB_RECURSE HOOK_SOURCES
        Hook/*.cpp
        JniHook/*.cpp
        Utils/*.cpp
)

# -------------------------
# Main shared library
# -------------------------
add_library(BoxCore SHARED
        BoxCore.cpp
        IO.cpp
        hidden_api.cpp
        ${HOOK_SOURCES}
)

target_include_directories(BoxCore PRIVATE
        ${CMAKE_CURRENT_SOURCE_DIR}
        ${CMAKE_CURRENT_SOURCE_DIR}/Hook
        ${CMAKE_CURRENT_SOURCE_DIR}/JniHook
        ${CMAKE_CURRENT_SOURCE_DIR}/Utils
        ${CMAKE_CURRENT_SOURCE_DIR}/xdl
)

# -------------------------
# Link libraries
# -------------------------
target_link_libraries(BoxCore
        log
        android
        xdl
        dobby
)
