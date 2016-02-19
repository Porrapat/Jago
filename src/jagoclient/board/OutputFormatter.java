package jagoclient.board;

public class OutputFormatter
{	static public int formtime (char a[], int sec)
	{	if (sec<0) sec=-sec;
		int min=sec/60;
		sec-=min*60;
		int h=min/60;
		min-=h*60;
		int n=0;
		if (h>0) 
		{	n=formint(a,n,h);
			a[n++]=':';
		}
		a[n++]=(char)('0'+min/10);
		a[n++]=(char)('0'+min%10);
		a[n++]=':';
		a[n++]=(char)('0'+sec/10);
		a[n++]=(char)('0'+sec%10);
		return n;
	}
	static public int formint (char a[], int offset, int h)
	{	int n=offset;
		if (h>0)
		{	int k=10;
			while (k<h) k*=10;
			k/=10;
			while (h>0)
			{	int temp=h/k;
				h-=temp*k;
				k/=10;
				a[n++]=(char)('0'+temp);
			}
		}
		else a[n++]='0';
		return n;		
	}
}