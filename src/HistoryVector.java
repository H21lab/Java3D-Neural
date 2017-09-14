
public class HistoryVector {
	private int value[] = new int[32];
	private int counter;

	public static long countZeroBits(long x) {
		int z = 0;
		for(int i = 0; i < 32; i++) {
			if (((x >> i)&1) == 0) {
				z++;
			}
		}
		return z;
	}
	
	HistoryVector() {
		super();
		
		for (int i = 0; i < value.length; i++) {
			this.value[i] = 0xffffffff;
		}
		counter = 0;
	}
	
	int[] getValue() {
		return value;
	}
	
	public void push(int i) {
		for (int idx = value.length - 1; idx >= 1; idx--) {
			this.value[idx] = this.value[idx] << 1;
			this.value[idx] = this.value[idx] | (this.value[idx - 1]>>>31);

			//System.out.println(Long.toBinaryString(this.value[idx]));
		}

		
//System.out.println(Integer.toBinaryString((0xffffffff)>>>32));
		
		this.value[0] = this.value[0] << 1;
		this.value[0] = this.value[0] | i;
		
		
	/*	// which bit will be written
		long mask = counter ^ (counter + 1);
		

		if (i == 1){
			value = (long)(value | mask);
		} else{
			value = (long)(value & ~mask);
		}*/
				
		counter++;
	}
	
	static public float corelate(HistoryVector a, HistoryVector e) {
		int A[] = a.getValue();
		int E[] = e.getValue();
		int r;
		float f = 0.0f;
		//= a.getValue() ^ b.getValue();
		
		for (int i = 0; i < A.length; i++) {
			r = A[i] ^ E[i];
			//System.out.println(Long.toBinaryString(A[i]));
			//System.out.println(Long.toBinaryString(B[i]));
			//System.out.println(f);	
			
			f += (0.5f/(float)(1<<i))*((float)countZeroBits(r)); 
			//f += (float)countZeroBits(r);
//System.out.println((1.0f/(float)(1<<i)));			
		}
		f = f / (32.0f*(float)A.length);

		
		//long r = a.getValue() ^ b.getValue();
		//float f = ((float)countZeroBits(r))/32.0f; 
		
//System.out.println(Long.toBinaryString(a.getValue()));
//System.out.println(Long.toBinaryString(b.getValue()));
//System.out.println(f);		
		return f;
	}
}
