package ceit.aut.ac.ir;

import weka.associations.AssociationRule;
import weka.associations.AssociationRules;
import weka.associations.BinaryItem;
import weka.associations.FPGrowth;
import weka.core.Instances;
import weka.core.converters.ConverterUtils.DataSource;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.util.*;


public class Q4 {

    public static void main(String args[]) throws Exception {

        double minSup = 0.1;
        int numRules = 10000000;


        String dataset = "C:\\Program Files\\Weka-3-9\\data\\supermarket.arff";
        DataSource source = new DataSource(dataset);
        Instances data = source.getDataSet();

        ArrayList<Collection<BinaryItem>> cPattern = new ArrayList<>();
        ArrayList<Collection<BinaryItem>> mPattern = new ArrayList<>();
        ArrayList<Collection<BinaryItem>> frequentPatterns = new ArrayList<>();
        ArrayList<Double> supports = new ArrayList<>();

        FPGrowth fpgrowth_model = new FPGrowth();
        fpgrowth_model.setNumRulesToFind(numRules);
        fpgrowth_model.setLowerBoundMinSupport(minSup);
        fpgrowth_model.setMinMetric(0);
        fpgrowth_model.buildAssociations(data);
        AssociationRules ARS = fpgrowth_model.getAssociationRules();
        List<AssociationRule> ruleList = ARS.getRules();


        for (int i = 0; i < ruleList.size(); i++) {

            AssociationRule AR = ruleList.get(i);

            Collection premise = AR.getPremise();
            int premiseSupport = AR.getPremiseSupport();

            Collection consequence = AR.getConsequence();
            int consequenceSupport = AR.getConsequenceSupport();

            int totalSupport = AR.getTotalSupport();
            Collection<BinaryItem> baseFrequentPattern = new HashSet<BinaryItem>();

            Iterator iterator = premise.iterator();
            while (iterator.hasNext()) {
                baseFrequentPattern.add((BinaryItem) iterator.next());
            }

            iterator = consequence.iterator();
            while (iterator.hasNext()) {
                baseFrequentPattern.add((BinaryItem) iterator.next());
            }

            if (!frequentPatterns.contains(baseFrequentPattern)) {
                frequentPatterns.add(baseFrequentPattern);
                supports.add((double) totalSupport / data.size());
            }
        }

        for (int i = 0; i < frequentPatterns.size(); i++) {
            Collection<BinaryItem> base = frequentPatterns.get(i);
            boolean closed = true;
            for (int j = 0; j < frequentPatterns.size(); j++) {

                if (frequentPatterns.get(j).equals(base)) {
                    continue;
                } else {
                    if (checkSupport(base, frequentPatterns.get(j))) {
                        if (supports.get(i) <= supports.get(j)) {
                            closed = false;
                            break;
                        }
                    }
                }
            }
            if (closed) {
                cPattern.add(base);
            }

        }

        BufferedWriter cWriter = new BufferedWriter(new FileWriter("./closedPatterns.txt"));
        for (int i = 0; i < cPattern.size(); i++) {
            cWriter.write(cPattern.get(i).toString() + "\n");
        }
        cWriter.close();

        for (int i = 0; i < frequentPatterns.size(); i++) {
            Collection<BinaryItem> base = frequentPatterns.get(i);
            boolean max = true;
            for (int j = 0; j < frequentPatterns.size(); j++) {

                if (frequentPatterns.get(j).equals(base)) {

                    continue;
                } else {
                    if (checkSupport(base, frequentPatterns.get(j))) {
                        if (supports.get(j) >= minSup) {
                            max = false;
                            break;
                        }
                    }
                }
            }
            if (max) {
                mPattern.add(base);
            }

        }

        BufferedWriter mWriter = new BufferedWriter(new FileWriter("./maxPatterns.txt"));
        for (int i = 0; i < mPattern.size(); i++) {
            mWriter.write(mPattern.get(i).toString() + "\n");
        }
        mWriter.close();
    }


    public static boolean checkSupport(Collection<BinaryItem> base, Collection<BinaryItem> super_s) {
        for (BinaryItem b : base) {
            if (!super_s.contains(b)) return false;
        }
        return true;
    }
}