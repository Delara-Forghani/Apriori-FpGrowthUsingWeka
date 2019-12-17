package ceit.aut.ac.ir;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.stage.Stage;
import weka.associations.Apriori;
import weka.associations.AssociationRules;
import weka.associations.FPGrowth;
import weka.core.Instances;
import weka.core.converters.ConverterUtils.DataSource;

import java.util.ArrayList;


public class Q3 extends Application {

    static ArrayList<Long> aTimes = new ArrayList<>();
    static ArrayList<Long> fTimes = new ArrayList<>();
    static String dataset = "C:\\Program Files\\Weka-3-9\\data\\supermarket.arff";
    static double[] minSup = {0.05, 0.06, 0.07, 0.08, 0.09, 0.1, 0.11, 0.12, 0.13, 0.14, 0.15};


    public static void main(String args[]) throws Exception {
        DataSource source = new DataSource(dataset);
        Instances data = source.getDataSet();


        for (int i = 0; i < minSup.length; i++) {

            String minSupport = String.valueOf(minSup[i]);
            String[] options = {"-N", "100000", "-C", "0.9", "-M", minSupport};

            long aStart = System.currentTimeMillis();
            Apriori apriori_model = new Apriori();
            apriori_model.setOptions(options);
            apriori_model.buildAssociations(data);
            AssociationRules ARS = apriori_model.getAssociationRules();
            ARS.getRules();
            long aEnd = System.currentTimeMillis();
            System.out.println("APriori: " + (aEnd - aStart));
            aTimes.add(aEnd - aStart);
        }
        for (int i = 0; i < minSup.length; i++) {
            String minSupport = String.valueOf(minSup[i]);
            String[] options = {"-N", "100000", "-C", "0.9", "-M", minSupport};

            long fStart = System.currentTimeMillis();
            FPGrowth fpgrowth_model = new FPGrowth();
            fpgrowth_model.setOptions(options);
            fpgrowth_model.buildAssociations(data);
            AssociationRules ARS = fpgrowth_model.getAssociationRules();
            ARS.getRules();
            long fEnd = System.currentTimeMillis();
            fTimes.add(fEnd - fStart);
            System.out.println("FP: " + (fEnd - fStart));
        }

        launch(args);


    }

    @Override
    public void start(Stage stage) throws Exception {
        stage.setTitle("Bar Chart Sample");
        final CategoryAxis xAxis = new CategoryAxis();
        final NumberAxis yAxis = new NumberAxis();
        final BarChart<String, Number> bc =
                new BarChart<String, Number>(xAxis, yAxis);
        bc.setTitle("Timing comparison between Apriori and FPGrowth");
        xAxis.setLabel("support");
        yAxis.setLabel("time in milliseconds");

        XYChart.Series series1 = new XYChart.Series();
        series1.setName("Apriori");
        XYChart.Series series2 = new XYChart.Series();
        series2.setName("FPGrowth");
        for (int i = 0; i < aTimes.size(); i++) {
            series1.getData().add(new XYChart.Data(String.valueOf(minSup[i]), aTimes.get(i)));
            series2.getData().add(new XYChart.Data(String.valueOf(minSup[i]), fTimes.get(i)));
        }


        Scene scene = new Scene(bc, 800, 800);
        bc.getData().addAll(series1, series2);
        stage.setScene(scene);
        stage.show();
    }
}
