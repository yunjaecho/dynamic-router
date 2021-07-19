package com.yunjae.dynamicrouter.controller.manager;

import com.yunjae.dynamicrouter.service.manger.route.RouterService;
import org.springframework.beans.factory.config.SingletonBeanRegistry;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.context.ApplicationContext;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class RouterController {

    private final ApplicationContext context;
    private final RouterService service;

    public RouterController(ApplicationContext context, RouterService service) {
        this.context = context;
        this.service = service;
    }

    @GetMapping("/mng/route/reload")
    public Object reloadRoute() {
//        ConfigurableApplicationContext configContext = (ConfigurableApplicationContext) context;
//        SingletonBeanRegistry beanRegistry = configContext.getBeanFactory();
//
//        Object routerFunction = beanRegistry.getSingleton("routerFunction");

        BeanDefinitionRegistry beanFactory =
                (BeanDefinitionRegistry) context.getAutowireCapableBeanFactory();
        beanFactory.removeBeanDefinition("routerFunction");




        ((SingletonBeanRegistry) beanFactory).registerSingleton("routerFunction", service.getServerResponseRouterFunction());

        return "success";
    }
}
