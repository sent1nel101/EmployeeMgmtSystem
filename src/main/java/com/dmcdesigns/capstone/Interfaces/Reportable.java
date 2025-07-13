package com.dmcdesigns.capstone.Interfaces;

import java.util.Map;

public interface Reportable {
    Map<String, Object> generateReport();
    String getReportTitle();
    String getReportSummary();
}
