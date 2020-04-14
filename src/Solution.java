import java.util.ArrayList;
import java.util.List;

interface MovingAverage {
    // add the element to the data structure
    void add(int o);
    // get the element based on the sequence added to the data structure
    int get(int i);
    double average();
}

/**
 * N: up to N element taken into calculation for average
 * list: used to store the elements
 * sum : sum of all the element that we will take into calculation
 */
class Nverage implements MovingAverage{
    int N;
    int sum;
    int start;
    List<Integer> list;
    public Nverage(int N){
        this.N = N;
        this.sum = 0;
        this.list = new ArrayList();
    }

    /**
     * @param e
     * two pointers so as to force sum contain up to N latest element
     */
    @Override
    public void add(int e) {
        this.list.add(e);
        this.sum += e;
        int end = list.size();
        if( end-this.start> this.N ){
            this.sum -= list.get(this.start);
            this.start ++;
        }
    }
    /**
     * @param i the sequence of the element that we want to access, 1 based
     * @return if i is larger than our current size, will return -1
     */
    @Override
    public int get(int i) {
        int index = i-1;
        if(index>=this.list.size()){
            return -1;
        }
        return this.list.get(index);
    }

    @Override
    public double average(){
        int size = this.list.size();
        if (size == 0){
            return 0;
        }
        size = Math.min(size, this.N);
        return (double)this.sum/size;
    }
}

public class Solution {
    public static void main(String[] args){
        int N = 3;
        int total = 20;
        Nverage nverage = new Nverage(N);
        for(int i=0;i<total;i++){
            int num = (int)(Math.random()*100);
            nverage.add(num);
            System.out.printf("The %d element %d added, last %d average is: %.2f\n", i+1, num, N, nverage.average()); // prints user1您好,您是第3位访客
        }
        for(int i=1;i<=total;i++){
            System.out.printf("Element %d is: %d\n", i, nverage.get(i)); // prints user1您好,您是第3位访客
        }
    }
}



