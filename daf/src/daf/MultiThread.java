package daf;

import java.util.Set;

public class MultiThread extends Thread {
	int[][] attacks;
	int[] set; 
	Set<int[]> complete_extensions;
	int size;
	public MultiThread(int[][] attacks, int[] subset, Set<int[]> complete_extensions) {
		this.attacks =attacks;
		this.complete_extensions=complete_extensions;
		this.set=subset;
		size=attacks.length;
	}

	public void run() 
	{ 
		try
		{ 
			// Displaying the thread that is running 
			System.out.println ("Thread " + 
					Thread.currentThread().getId() + 
					" is running"); 
			//System.out.println("}");
			if(conflictFree()) {
				if(admissible()) {
					if(complete()) {
						complete_extensions.add(set);
						//print(subset);
					}
				}
			}

		} 
		catch (Exception e) 
		{ 
			// Throwing an exception 
			System.out.println ("Exception is caught"); 
		} 
	} 
	
	/*
	 * A set is admissible if every time one of its members is attacked by an argument, then this argument is attacked by the set
	 */
	public boolean admissible() {
		//iterate over members of the set
		for(int i=0; i<set.length; i++) {
			if(set[i]==1) {
				//identify attackers of the member
				for(int j=0; j<size; j++ ) {
					if(attacks[j][i]==1) {
						boolean attacked_not_defended = true;
						for(int k=0; k<set.length;k++) {
							if(set[k]==1) {
								if(attacks[k][j]==1) {
									attacked_not_defended =false;
									break;
								}
							}
						}
						if(attacked_not_defended) {
							return false;
						}
					}
				}
			}
		}
		return true;
	}
	/*
	 * A set is conflict free if it does not include two members who attack each other
	 */
	public boolean conflictFree() {
		for(int i=0; i<set.length; i++) {
			if(set[i]==1) {
				for(int j=i+1; j<set.length; j++ ) {
					if(set[j]==1) {
						if(attacks[i][j]==1 || attacks[j][i]==1) {
							return false;
						}
					}
				}
			}
		}
		return true;
	}
	/*
	 * A set is complete if it includes all arguments that are acceptable w.r.t. it
	 */
	public  boolean complete() {
		//iterate over arguments
		//iterate over members of the set
		
		for(int i=0; i<size; i++) {
			//identify attackers of the member
			boolean acceptable =true;
			for(int j=0; j<size; j++ ) {
				if(attacks[j][i]==1) {
					acceptable=false;
					for(int k=0; k<set.length;k++) {
						if(set[k]==1) {
							if(attacks[k][j]==1) {
								acceptable=true;
							}
						}
					}
					if(acceptable && !in_conflict_with_set(i,set) && set[i]==0) {
						return false;
					}
				}
			}
		}
		return true;
	}


	private  boolean in_conflict_with_set(int v, int[] set) {
		for(int i=0;i<set.length;i++) {
			if(set[i]==1) {
				if(attacks[i][v]==1 || attacks[v][i]==1) {
					return true;
				}
			}
		}
		return false;
	}
} 



