#include <stdlib.h>
void* memory_c_allocate(size_t size) { return malloc(size); }
void memory_c_free(void* ptr) { free(ptr); }
