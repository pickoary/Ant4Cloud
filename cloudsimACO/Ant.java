package org.cloudbus.cloudsim;

import java.util.*;

import org.cloudbus.cloudsim.Cloudlet;
import org.cloudbus.cloudsim.Vm;

/**
 *������
 *@author ShiJianYuan
 */
 public class Ant{
	public class position{
		public int vm;
		public int task;
		public position(int a, int b){
			vm = a;
			task = b;
		}
	} 
	public double[][] delta;//ÿ���ڵ����ӵ���Ϣ��
	public int Q = 100;
	public List<position> tour;//���ϻ�õ�·�����⣬��������������ķַ���
	public double tourLength;//���ϻ�õ�·�����ȣ�����ú��ܵĻ���ʱ�䣩
	public long[] TL_task;//ÿ�����������������
	public List<Integer> tabu;//���ɱ�
	private int VMs;//���еĸ������൱��������ĸ�����
	private int tasks;//�������
	private List<? extends Cloudlet> cloudletList;	//�������б�
	private List<? extends Vm> vmList;				//������б�
	/**
	 *����������ϵ�ĳ���ڵ��У�ͬʱ������ϰ����ֶεĳ��Ի�����
	 *@param list1 �����б�
	 *@param list2 ������б�
	 */
	public void RandomSelectVM(List<? extends Cloudlet> list1, List<? extends Vm> list2){
		cloudletList = list1;
		vmList = list2;
		VMs = vmList.size();
		tasks = cloudletList.size();
		delta = new double[VMs][tasks];
		TL_task = new long[VMs];
		for(int i=0; i<VMs; i++)TL_task[i] = 0;
		tabu = new ArrayList<Integer>();
		tour=new ArrayList<position>();
		
		//���ѡ�����ϵ�λ��
		int firstVM = (int)(VMs*Math.random());
		int firstExecute = (int)(tasks*Math.random());
		tour.add(new position(firstVM, firstExecute));
		tabu.add(new Integer(firstExecute));
		TL_task[firstVM] += cloudletList.get(firstExecute).getCloudletLength();
	}
	/**
	  * calculate the expected execution time and transfer time of the task on vm
	  * @param vm ��������
	  * @param task �������
	  */
	public double Dij(int vm, int task){
		double d;
	    d = TL_task[vm]/vmList.get(vm).getMips() + cloudletList.get(task).getCloudletLength()/vmList.get(vm).getBw();
		return d;
	}
	 /**
	  * ѡ����һ���ڵ�
	  * @param pheromone ȫ�ֵ���Ϣ����Ϣ
	  */
	  public void SelectNextVM(double[][] pheromone){
		  double[][] p;//ÿ���ڵ㱻ѡ�еĸ���
		  p = new double[VMs][tasks];
		  double alpha = 1.0;
		  double beta = 1.0;
		  double sum = 0;//��ĸ
		  //���㹫ʽ�еķ�ĸ����  
		  for(int i=0; i<VMs; i++){
			  for(int j=0; j<tasks; j++){
				  if(tabu.contains(new Integer(j))) continue;
				  sum += Math.pow(pheromone[i][j], alpha)*Math.pow(1/Dij(i,j),beta);
			  }
		  }
		  //����ÿ���ڵ㱻ѡ�ĸ���
		  for(int i=0; i<VMs; i++){
			  for(int j=0; j<tasks; j++){
				  p[i][j] = Math.pow(pheromone[i][j], alpha)*Math.pow(1/Dij(i,j),beta)/sum;
				  if(tabu.contains(new Integer(j)))p[i][j] = 0;
			  }
		  }
		double selectp = Math.random();
        //���̶�ѡ��һ��VM
        double sumselect = 0;
        int selectVM = -1;
        int selectTask = -1;
        boolean flag=true;
        for(int i=0; i<VMs&&flag==true; i++){
        	for(int j=0; j<tasks; j++){
        		sumselect += p[i][j];
        		if(sumselect>=selectp){
        			selectVM = i;
        			selectTask = j;
        			flag=false;
        			break;
        		}
        	}
        }
        if (selectVM==-1 | selectTask == -1)  
            System.out.println("ѡ����һ�������û�гɹ���");
    		tabu.add(new Integer(selectTask));
		tour.add(new position(selectVM, selectTask));
		TL_task[selectVM] += cloudletList.get(selectTask).getCloudletLength();  		
	  }
	  
	  
	  
	public void CalTourLength(){
		System.out.println();
		double[] max;
		max = new double[VMs];
		for(int i=0; i<tour.size(); i++){
			max[tour.get(i).vm] += cloudletList.get(tour.get(i).task).getCloudletLength()/vmList.get(tour.get(i).vm).getMips(); 
		}		
		tourLength = max[0];
		for(int i=0; i<VMs; i++){
			if(max[i]>tourLength)tourLength = max[i];
			System.out.println("��"+i+"̨�������ִ��ʱ�䣺"+max[i]);
		}
		return;
	}
	/**
	 * ������Ϣ����������
	 */
    public void CalDelta(){
    	for(int i=0; i<VMs; i++){
    		for(int j=0; j<tasks; j++){
    			if(i==tour.get(j).vm&&tour.get(j).task==j)delta[i][j] = Q/tourLength;
    			else delta[i][j] = 0;
    		}
    	}
    }
 }