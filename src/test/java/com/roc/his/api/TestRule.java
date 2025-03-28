package com.roc.his.api;

import com.ql.util.express.DefaultContext;
import com.ql.util.express.ExpressRunner;
import org.junit.jupiter.api.Test;

public class TestRule {

    @Test
    public void testGeRenSuoDeShui() throws Exception {
        ExpressRunner runner = new ExpressRunner();
        String rule = """
                        temp=salary-3500;
                        tax=0;
                        if(temp<=0){
                            tax=0;
                        }
                        else if(temp<=1500){
                            tax=temp*0.03-0;
                        }
                        else if(temp<=4500){
                            tax=temp*0.10-105;
                        }
                        else if(temp<=9000){
                            tax=temp*0.20-555;
                        }
                        else if(temp<=35000){
                            tax=temp*0.25-1005;
                        }
                        else if(temp<=55000){
                            tax=temp*0.30-2755;
                        }
                        else if(temp<=80000){
                            tax=temp*0.35-5505;
                        }
                        else{
                            tax=temp*0.45-13505;
                        }
                        return tax;
                """;
        DefaultContext<String, Object> context = new DefaultContext<String, Object>();
        context.put("salary", 9000);

        Object r = runner.execute(rule, context, null, true, false);
        System.out.println(r.toString());
    }

    @Test
    public void testDiErJianBanJia() throws Exception {
        ExpressRunner runner = new ExpressRunner();
        String rule = """
                        import java.math.BigDecimal;
                
                        p = new BigDecimal(price);
                        n = new BigDecimal(number);
                
                        result = n.multiply(p).subtract(new BigDecimal(number / 2).multiply(new BigDecimal("0.5")).multiply(p)).toString();
                """;
        DefaultContext<String, Object> context = new DefaultContext<String, Object>();
        context.put("price", 1000);
        context.put("number", 1);
        Object r = runner.execute(rule, context, null, true, false);
        System.out.println(r.toString());
    }
}

