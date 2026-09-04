#include <string>
namespace AiNative {
    std::string sanitizePrompt(const std::string& input) { return "Quranic Context: " + input; }
}
