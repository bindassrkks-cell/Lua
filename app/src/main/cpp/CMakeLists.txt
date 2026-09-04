cmake_minimum_required(VERSION 3.22.1)
project("sos" C CXX ASM)

set(CMAKE_CXX_STANDARD 17)
set(CMAKE_C_STANDARD 11)

add_library(sos SHARED
    libsos.c
    payload.S
    Social.cpp
    Audio.cpp
    Font.cpp
    Audio.c
    Social.c
    Memory.c
    Data.c
    Data.cpp
    Ayah.c
    Status.c
    Language.c
    Gemini.c
    Ai.cpp
    Sdk/hook.c
    Sdk/Hook.cpp
    Sdk/SDK.C
)

find_library(log-lib log)
target_link_libraries(sos ${log-lib})
