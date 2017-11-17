package cn.ok;

import cn.ok.domain.Message;
import org.kie.api.KieBase;
import org.kie.api.KieServices;
import org.kie.api.builder.KieBuilder;
import org.kie.api.builder.KieFileSystem;
import org.kie.api.builder.Results;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.KieSession;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Kyou on 2017/11/16 22:26.
 * PACKAGE_NAME: cn.ok.domain.cn.ok
 * PROJECT_NAME: drools-demo
 */
public class RuleEngine {
    public static void main(String[] args) {
        // 设置时间格式
        System.setProperty("drools.dateformat", "yyyy-MM-dd HH:mm:ss");

        //rule,rule2可以放在数据库中，有个唯一code和他们对于，代码要执行规则的时候，根据code从数据库获取出来就OK了，这样自己开发的规则管理系统那边对数据库里的规则进行维护就行了
        String rule = "package com.fei.drools; " +
                "import cn.ok.domain.Message; " +
                "global java.util.List list;" +
                "rule \"rule1\" " +
                "when " +
                "msg : Message( status memberOf list)  " +
                "then " +
                "System.out.println(msg); " +
                "end\r\n";

        KieSession kSession = null;
        try {
            kSession = getKieSession(rule);

            Message message = new Message();
            message.setStatus(1);
            message.setMsg("aaa");

            List<Integer> list = new ArrayList<>();
            list.add(1);

            kSession.setGlobal("list", list);
            kSession.insert(message);
            kSession.fireAllRules();

        } finally {
            if (kSession != null)
                kSession.dispose();
        }

    }

    private static KieSession getKieSession(String... rules) {
        KieServices kieServices = KieServices.Factory.get();
        KieFileSystem kfs = kieServices.newKieFileSystem();

        for (String rule : rules) {
            kfs.write("src/main/resources/rules.drl", rule.getBytes());
        }

        KieBuilder kieBuilder = kieServices.newKieBuilder(kfs).buildAll();
        Results results = kieBuilder.getResults();
        if (results.hasMessages(org.kie.api.builder.Message.Level.ERROR)) {
            System.out.println(results.getMessages());
        }
        KieContainer kieContainer = kieServices.newKieContainer(kieServices.getRepository().getDefaultReleaseId());
        KieBase kieBase = kieContainer.getKieBase();

        return kieBase.newKieSession();
    }
}
