    public void configure(TestElement element)
    {
        if (! initialized) init();

        super.configure(element);

        // Copy all property values into the map:
        propertyMap.clear();
        for (PropertyIterator jprops= element.propertyIterator(); jprops.hasNext(); )
        {
            JMeterProperty jprop= jprops.next();
            propertyMap.put(jprop.getName(), jprop.getObjectValue());
        }

        if (customizer != null)
        {
            customizer.setObject(propertyMap);
        }
        else
        {
            if (initialized) remove(customizerIndexInPanel);
            Customizer c= (Customizer)customizers.get(element);
            if (c == null)
            {
                c= createCustomizer();
                c.setObject(propertyMap);
                customizers.put(element, c);
            }
            add((Component)c, BorderLayout.CENTER);
        }

        initialized= true;
    }
