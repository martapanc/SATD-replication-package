    public String execute(SampleResult previousResult, Sampler currentSampler)
            throws InvalidVariableException {
        // return JMeterContextService.getContext().getCurrentSampler().getName();
        String name = "";
        if (currentSampler != null) { // will be null if function is used on TestPlan
            name = currentSampler.getName();
        }
        if (values.length > 0){
            JMeterVariables vars = getVariables();
            if (vars != null) {// May be null if function is used on TestPlan
                String varName = ((CompoundVariable) values[0]).execute().trim();
                if (varName.length() > 0) {
                    vars.put(varName, name);
                }
            }
        }
        return name;
    }
