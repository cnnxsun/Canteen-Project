//Name: Chaninan Phetpangun
//ID: 6488061
//Section: 2

import java.util.ArrayList;
import java.util.List;

public class Customer {
	
	//*********************** DO NOT MODIFY ****************************//
	public static enum CustomerType{DEFAULT, STUDENT, PROFESSOR, ATHLETE, ICTSTUDENT};	//Different types of customers 
	private static int customerRunningNumber = 1;	//static variable for assigning a unique ID to a customer
	private CanteenICT canteen = null;	//reference to the CanteenICT object
	private int customerID = -1;		//this customer's ID
	protected CustomerType customerType = CustomerType.DEFAULT;	//the type of this customer, initialized with a DEFAULT customer.
	protected List<FoodStall.Menu> requiredDishes = new ArrayList<FoodStall.Menu> ();	//List of required dishes
	//*****************************************************************//
	private int state = 0;
	private int eatingTime = 0;
	FoodStall myfs = null;
	Table mytable =null;
	
	
	Customer(CanteenICT canteen)
	{
		//******************* YOUR CODE HERE **********************
		//initialize variable
		this.customerID = customerRunningNumber++;
        this.canteen = canteen;
        //add menu(สามารถทำเป็นลูปได้)

        this.requiredDishes.clear();
        this.requiredDishes.add(FoodStall.Menu.NOODLES);
        this.requiredDishes.add(FoodStall.Menu.DESSERT);
        this.requiredDishes.add(FoodStall.Menu.MEAT);
        this.requiredDishes.add(FoodStall.Menu.SALAD);
        this.requiredDishes.add(FoodStall.Menu.BEVERAGE);
        this.myfs = null;
        this.mytable = null;
		
        state = 1;
        
        for(FoodStall.Menu dish : FoodStall.Menu.values()) //เวลาในการกิน
        {
        	this.eatingTime += FoodStall.EAT_TIME[dish.ordinal()];
        }

		//*****************************************************
	}
	public void eat() //เวลากิน
	{
		this.eatingTime--;
	}
	
	
	
	public void takeAction()
	{
		//************************** YOUR CODE HERE **********************//
		if(this.state==1) //ต่อแถวเข้าโรงอาหาร
		{
			if(canteen.getwaitToEnterQueue() != null 
					&& !canteen.getwaitToEnterQueue().isEmpty()
					&& canteen.getwaitToEnterQueue().get(0).equals(this))
			{
				this.canteen.waitToEnterQueueNext.clear();
				for(FoodStall fs : canteen.getFoodStallsList()) //หาfsที่คิวสั้นสุดและมีอาหารที่ต้องการ
				{
					//select fs
					//1. that sells all food in this.requiredDishes
					//2. that has shortest and still available customerQueue
					//3. that has lowest queue
					if(fs==canteen.getFindFsMinQueue() 
							&& fs.getCustomerQueue().size()<FoodStall.MAX_QUEUE
							&& fs.getMenu().containsAll(this.requiredDishes))
					{
						this.myfs=fs;
					}
				}
				if(this.myfs != null)
				{
					this.myfs.customerQueueNext.add(this);
					this.state = 2;
				}
				else
				{
					this.canteen.waitToEnterQueueNext.add(this);
				}
			}
			else //not front customer
			{
				this.canteen.waitToEnterQueueNext.add(this);
			}
		}
		else if(this.state==2) //ต่อคิวรออาหาร
		{
			if(this.myfs.getCustomerQueue() != null 
					&& !this.myfs.getCustomerQueue().isEmpty() 
					&& this.myfs.getCustomerQueue().get(0).equals(this))
			{
				if(this.myfs.isWaitingForOrder()) //สั่งอาหาร
				{
					this.myfs.takeOrder(this.requiredDishes);
					this.state = 3;
				}
			}
		}
		else if(this.state==3)//รอรับอาหารต่อแถวหาโต๊ะ
		{
			if(this.myfs.getCustomerQueue() != null 
					&& !this.myfs.getCustomerQueue().isEmpty() 
					&& this.myfs.getCustomerQueue().get(0).equals(this))
			{
				if(this.myfs.isReadyToServe()) //ถ้าพร้อมเสิร์ฟแล้วให้ย้ายไปwaitToSeatQueueNext
				{
					this.myfs.serve();
					this.myfs.customerQueueNext.remove(this);
					this.canteen.waitToSeatQueueNext.add(this);
					this.state = 4;
				}
			}
		}
		else if(this.state==4) //หาโต๊ะ
		{
			if(this.canteen.getwaitToSeatQueue() != null 
					&& !this.canteen.getwaitToSeatQueue().isEmpty() 
					&& this.canteen.getwaitToSeatQueue().get(0).equals(this)){
				this.mytable = canteen.findTable();
				if(this.mytable != null)
				{
					this.canteen.waitToSeatQueueNext.remove(this);
					this.mytable.seatedCustomersNext.add(this);
					this.state = 5;
				}
			}
		}
		else if(this.state==5) //กินข้าว ถ้าeatingTimeหมดให้ย้ายไปdoneQueue
		{
			this.eat();
			if(this.eatingTime <= 0)
			{
				this.canteen.getDoneQueue().add(this);
			    this.mytable.seatedCustomersNext.remove(this);
				this.state = 6;
				
			}
		}
				
		//**************************************************************//
		
	}

	//***************For hashing, equality checking, and general purposes. DO NOT MODIFY **************************//	
	
	public CustomerType getCustomerType()
	{
		return this.customerType;
	}
	
	public int getCustomerID()
	{
		return this.customerID;
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + customerID;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Customer other = (Customer) obj;
		if (customerID != other.customerID)
			return false;
		return true;
	}
	
	@Override
	public String toString() {
		return "Customer [customerID=" + customerID + ", customerType=" + customerType +"]";
	}

	public String getCode()
	{
		return this.customerType.toString().charAt(0)+""+this.customerID;
	}
	
	/**
	 * print something out if VERBOSE is true 
	 * @param str
	 */
	public void jot(String str)
	{
		if(CanteenICT.VERBOSE) System.out.println(str);
		
		if(CanteenICT.WRITELOG) CanteenICT.append(str, canteen.name+"_state.log");
	}
	
	//*************************************************************************************************//
	
}
