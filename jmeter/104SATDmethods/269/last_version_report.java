    private void init()
    {
        setLayout(new BorderLayout(0, 5));

        setBorder(makeBorder());
        add(makeTitlePanel(), BorderLayout.NORTH);

        customizerIndexInPanel= getComponentCount();

        if (customizerClass == null)
        {
            customizer= new GenericTestBeanCustomizer(beanInfo);
        }
        else if (SharedCustomizer.class.isAssignableFrom(customizerClass))
        {
            customizer= createCustomizer();
        }
        
        if (customizer != null) add((Component)customizer, BorderLayout.CENTER);
    }
