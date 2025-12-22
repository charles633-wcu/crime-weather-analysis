package com.charleswang.analytics.app.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.charleswang.analytics.app.client.DataServiceClient;
import com.charleswang.analytics.app.model.DailyPoint;
import com.charleswang.analytics.app.util.MathUtils;

public class RegressionService {

    public static Map<String, Object> computeRegression() throws Exception {

        List<DailyPoint> points = DataServiceClient.fetchDailyPoints();

        double beta = MathUtils.slope(points);
        double alpha = MathUtils.intercept(points, beta);
        double r2 = MathUtils.rSquared(points, alpha, beta);

        Map<String, Object> result = new HashMap<>();
        result.put("alpha", alpha);
        result.put("beta", beta);
        result.put("r_squared", r2);
        result.put("observations", points.size());

        return result;
    }
}
