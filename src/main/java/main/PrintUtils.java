package main;

import org.apache.commons.math3.linear.RealMatrix;
import org.apache.commons.math3.linear.RealVector;

public class PrintUtils {

    public PrintUtils() {
    }

    public void printVector(RealVector v) {
        System.out.print("[");
        for(int i=0; i<v.getDimension(); i++){
            System.out.print(String.format("%5d,",Math.round(v.getEntry(i))));
        }
        System.out.println("]");
    }

    public void printMatrix(RealMatrix m){
        System.out.print("[");
        for(int i=0; i<m.getRowDimension(); i++){
            System.out.print("[");
            for(int j=0; j<m.getColumnDimension(); j++){
                System.out.print(String.format("%5d, ",Math.round(m.getEntry(i,j))));
            }
            System.out.println("],");
        }
        System.out.println("]");
    }
}
