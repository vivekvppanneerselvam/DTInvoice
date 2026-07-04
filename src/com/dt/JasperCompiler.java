package com.dt;

import net.sf.jasperreports.engine.JasperCompileManager;

public class JasperCompiler {

    public static void main(String[] args) {
        //new_dt_invoice_final_FINAL
        try {

            JasperCompileManager.compileReportToFile(

                    "D:/WS/java/DTInvoice/src/resources/reports/new_design_dt_invoice.jrxml",

                    "D:/WS/java/DTInvoice/src/resources/reports/new_dt_invoice_final1.jasper"
            );

            System.out.println(
                    "Jasper Compiled Successfully"
            );

        } catch (Exception e) {

            e.printStackTrace();
        }
    }
}