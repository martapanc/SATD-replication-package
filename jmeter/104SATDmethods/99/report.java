File path: src/functions/org/apache/jmeter/functions/SamplerName.java
Comment: TODO Should this method be synchronized ? all other function execute are
Initial commit id: 1a3195d8d
Final commit id: 63c750cac
   Bugs between [       3]:
63c750cac Bug 57114 - Performance : Functions that only have values as instance variable should not synchronize execute Remove old TODO Bugzilla Id: 57114
f03a8bdbe Bug 57114 - Performance : Functions that only have values as instance variable should not synchronize execute Bugzilla Id: 57114
ea92414e2 Bug 54199 - Move to Java 6 add @Override Bugzilla Id: 54199
   Bugs after [       0]:


Start block index: 48
End block index: 68

    // TODO Should this method be synchronized ? all other function execute are
    /** {@inheritDoc} */
    @Override
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

*********************** Method when SATD was removed **************************

@Override
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
