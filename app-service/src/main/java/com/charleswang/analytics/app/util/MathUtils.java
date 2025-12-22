package com.charleswang.analytics.app.util;

import java.util.List;

import com.charleswang.analytics.app.model.DailyPoint;

public class MathUtils {

    public static double meanX(List<DailyPoint> pts) {
        return pts.stream().mapToDouble(DailyPoint::temperature).average().orElse(0);
    }

    public static double meanY(List<DailyPoint> pts) {
        return pts.stream().mapToDouble(DailyPoint::incidents).average().orElse(0);
    }

    public static double slope(List<DailyPoint> pts) {
        double xBar = meanX(pts);
        double yBar = meanY(pts);

        double num = 0, den = 0;
        for (DailyPoint p : pts) {
            num += (p.temperature() - xBar) * (p.incidents() - yBar);
            den += Math.pow(p.temperature() - xBar, 2);
        }
        return den == 0 ? 0 : num / den;
    }

    public static double intercept(List<DailyPoint> pts, double beta) {
        return meanY(pts) - beta * meanX(pts);
    }

    public static double rSquared(List<DailyPoint> pts, double alpha, double beta) {
        double yBar = meanY(pts);
        double ssTot = 0, ssRes = 0;

        for (DailyPoint p : pts) {
            double predicted = alpha + beta * p.temperature();
            ssTot += Math.pow(p.incidents() - yBar, 2);
            ssRes += Math.pow(p.incidents() - predicted, 2);
        }
        return ssTot == 0 ? 0 : 1 - (ssRes / ssTot);
    }
}
