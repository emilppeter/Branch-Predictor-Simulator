import java.io.File;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.lang.reflect.Array;
import java.text.DecimalFormat;
import java.util.Arrays;
import java.util.Scanner;

public class sim 
{
	static int M1;
	static int M2;
	static int N;
	static String trace_file;
	static char chars;
	static int K;
	static String t;
	int[] contents;
	static int[] chooser;
	static String BHR;
	static String PC;
	static String type;
	static int miss=0,access=0;
	static sim gshare;
	static sim bimodal;
	static sim hybrid;
	public static void main(String[] args) throws Exception 
	{
		char[] chars1;
		int[] contents;
		String binary;
		int i=0;
		int PCnum=0,num1=0,num=0;
		boolean a=false,b=false;
		gshare=new sim();
		bimodal=new sim();
		hybrid=new sim();
		type=args[0];
		if (type.compareTo("bimodal")==0)
		{
			M2=Integer.parseInt(args[1]);
			trace_file=args[2];
			contents=new int [(int) Math.pow(2, M2)];
			chars1= new char[M2];
			Arrays.fill(chars1,'0');
			PC=new String(chars1);
			bimodal.contents=new int [(int) Math.pow(2, M2)];
			for(int j=0;j<bimodal.contents.length;j++)
				bimodal.contents[j]=2;
		}
		else if (type.compareTo("gshare")==0)
		{
			M1=Integer.parseInt(args[1]);
			N=Integer.parseInt(args[2]);
			trace_file=args[3];
			contents=new int [(int) Math.pow(2, M1)];
			char[] charc= new char[N];
			Arrays.fill(charc,'0');
			BHR= new String(charc);
			chars1= new char[N];
			Arrays.fill(chars1,'0');
			PC=new String(chars1);
			gshare.contents=new int [(int) Math.pow(2, M1)];
			for(int j=0;j<gshare.contents.length;j++)
				gshare.contents[j]=2;
		}
		else if (type.compareTo("hybrid")==0)
		{
			K=Integer.parseInt(args[1]);
			M1=Integer.parseInt(args[2]);
			N=Integer.parseInt(args[3]);
			M2=Integer.parseInt(args[4]);
			trace_file=args[5];
			char[] charc= new char[N];
			Arrays.fill(charc,'0');
			BHR= new String(charc);
			gshare.contents=new int [(int) Math.pow(2, M1)];
			for(int j=0;j<gshare.contents.length;j++)
				gshare.contents[j]=2;
			bimodal.contents=new int [(int) Math.pow(2, M2)];
			for(int j=0;j<bimodal.contents.length;j++)
				bimodal.contents[j]=2;
			chooser=new int[(int)Math.pow(2, K)];
			for(int j=0;j<chooser.length;j++)
				chooser[j]=1;
		}
		File file = new File(trace_file); 
		Scanner sc = new Scanner(file);
		while (sc.hasNextLine())
		{	     
			String input_data=sc.nextLine();
			a=false;
			b=false;
			if (type.compareTo("gshare")==0)
			{
				binary=gshare.generate(input_data);	
				PCnum=gshare.gshareprocess(binary,M1);
				gshare.Contents(PCnum);
				if (chars=='t')
					BHR='1'+BHR;
				else if (chars=='n')
					BHR='0'+BHR;
				BHR=BHR.substring(0, BHR.length()-1);
			}
			else if (type.compareTo("bimodal")==0)
			{
				binary=bimodal.generate(input_data);	
				PCnum=bimodal.gshareprocess(binary,M2);
				bimodal.Contents(PCnum);
			}
			else if (type.compareTo("hybrid")==0)
			{
				binary=hybrid.generate(input_data);
				PCnum=hybrid.gshareprocess(binary, K);
				num=PCnum;
				binary=gshare.generate(input_data);	
				PCnum=gshare.gshareprocess(binary,M1);
				num1=PCnum;
				binary=bimodal.generate(input_data);	
				PCnum=bimodal.gshareprocess(binary,M2);			
				if (((gshare.contents[num1]>=2 && chars=='t') || (gshare.contents[num1]<2 && chars=='n')))
					a=true;
				if (((bimodal.contents[PCnum]>=2 && chars=='t') || (bimodal.contents[PCnum]<2 && chars=='n')))
					b=true;
				if (chooser[num]>=2)
					gshare.Contents(num1);
				else
					bimodal.Contents(PCnum);
				if (chars=='t')
					BHR='1'+BHR;
				else if (chars=='n')
					BHR='0'+BHR;
				BHR=BHR.substring(0, BHR.length()-1);
				if ((a==true) && (chooser[num]!=3) && (b==false))
					chooser[num]++;
				else if ((b==true) && (chooser[num]!=0) && (a==false))
					chooser[num]--;
				if (a==true && b==true)
					i++;
				
			}
			access++;
		}
		Display();	
	}
	public static String generate(String input_data)
	{
		int address_bits;
		String address = null;
		int len=input_data.length();
		chars=input_data.charAt(input_data.length()-1);
		address=input_data.substring(0,(input_data.length()-2));
		address_bits=Integer.parseInt(address,16);
		String binary=Integer.toBinaryString(address_bits);
		return (binary);
	}
	public static int gshareprocess(String binary,int index ) 
	{
		binary=binary.substring((binary.length()-2-index),(binary.length()-2));
		if (index==M1)
		{
			String binary1 = binary.substring(0, N);
			String binary2= binary.substring(N, M1);
			StringBuilder sb= new StringBuilder(PC);
			for(int i=0;i<N;i++)
				sb.insert(i, binary1.charAt(i)^BHR.charAt(i));
			PC=sb.toString();
			PC=PC.substring(0, N);
			PC+=binary2;
		}
		else 
		{
			PC=binary;
		}
		int PCnum=Integer.parseInt(PC,2);
		return PCnum;
	}
	public void Contents(int PCnum)
	{
		switch(this.contents[PCnum])
		{
			case 0:	if (chars=='t') 
					{
						this.contents[PCnum]++;
						miss++;
					}
					break;
			case 1:	if (chars=='t') 
					{
						this.contents[PCnum]++;
						miss++;
					}
					else if (chars=='n')
						this.contents[PCnum]--;
					break;
			case 2:	if (chars=='t') 
						this.contents[PCnum]++;
					else if (chars=='n')
					{
						this.contents[PCnum]--;
						miss++;
					}
					break;
			case 3:	if (chars=='n') 
					{
						this.contents[PCnum]--;
						miss++;
					}
					break;		
		}
	}
	public static void Display()
	{
		DecimalFormat f= new DecimalFormat("##.00");
		System.out.println("number of predictions:"+"\t"+access);
		System.out.println("number of mispredictions:"+"\t"+miss);
		System.out.println("misprediction rate:"+"\t"+f.format((((double)miss/access)*100))+"%");
		if (type.compareTo("hybrid")==0)
		{
			System.out.println("FINAL CHOOSER CONTENTS");
			for (int i=0;i<chooser.length;i++)
			{
				System.out.println(i+"\t"+chooser[i]);
			}
		}
		if ((type.compareTo("gshare")==0) || (type.compareTo("hybrid")==0) )
		{	
			System.out.println("FINAL GSHARE CONTENTS");
			for (int i=0;i<gshare.contents.length;i++)
			{
				System.out.println(i+"\t"+gshare.contents[i]);
			}
		}
		if ((type.compareTo("bimodal")==0) || (type.compareTo("hybrid")==0))
		{
			System.out.println("FINAL BIMODAL CONTENTS");
			for (int i=0;i<bimodal.contents.length;i++)
			{
				System.out.println(i+"\t"+bimodal.contents[i]);
			}
		}
	}
	public void initial(int index)
	{
		this.contents=new int [(int) Math.pow(2, index)];
	}
	}
	