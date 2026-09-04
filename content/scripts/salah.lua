local currentStep = 1
function onInit()
    Bridge:log("CorePatch Salah Lua Engine Active")
end
function nextStep()
    currentStep = currentStep + 1
    if currentStep > 4 then
        currentStep = 1
    end
    Bridge:log("Transition to Salah Step: " .. tostring(currentStep))
    return currentStep
end
