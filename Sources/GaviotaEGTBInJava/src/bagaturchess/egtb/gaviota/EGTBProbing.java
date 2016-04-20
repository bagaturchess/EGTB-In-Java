

/*
 *  Java API of Gaviota Tablebases Probing Code
 *  Copyright (c) 2012 Krasimir I. Topchiyski
 *
 *  This Software is distributed with MIT License but
 *  is consisting of other parts distributed under their own (and possibly different) licenses.
 *  See LICENSE in LICENSE.txt for more details
 *  
 *  Open Source project location: http://sourceforge.net/projects/egtb-in-java/
 *  SVN repository https://svn.code.sf.net/p/egtb-in-java/code-0
 */


package bagaturchess.egtb.gaviota;


public class EGTBProbing {
    
    
    public final static int NATIVE_WHITE_TO_MOVE	= 0;
    public final static int NATIVE_BLACK_TO_MOVE 	= 1;
    
    public final static int NATIVE_INFO_DRAW		= 0;
    public final static int NATIVE_INFO_WMATE		= 1;
    public final static int NATIVE_INFO_BMATE		= 2;
    public final static int NATIVE_INFO_FORBID		= 3;
    public final static int NATIVE_INFO_UNKNOWN		= 7;
    
    
    public final static int NATIVE_PID_NONE			= 0;
    public final static int NATIVE_PID_PAWN			= 1;
    public final static int NATIVE_PID_KNIGHT		= 2;
    public final static int NATIVE_PID_BISHOP  		= 3;
    public final static int NATIVE_PID_ROOK			= 4;
    public final static int NATIVE_PID_QUEEN		= 5;
    public final static int NATIVE_PID_KING			= 6;
    
    public final static int NATIVE_SQUARE_NONE		= 64;
    
    
    private static EGTBProbing singleton 			= null;
    
    
    private EGTBProbing() {
    }
    
    
    public static EGTBProbing getSingleton() {
 
		if (singleton == null) {
			synchronized(EGTBProbing.class) {
				if (singleton == null) {
					
			    	if (getJVMBitmode() == 64) {
			            System.loadLibrary("egtbprobe_64");
					} else { //32
				        System.loadLibrary("egtbprobe_32");
					}
			    	
					singleton = new EGTBProbing();
				}
			}
		}
		return singleton;
    }
	
	
	private static int getJVMBitmode() {
		
	    String vendorKeys [] = {
		        "sun.arch.data.model",
		        "com.ibm.vm.bitmode",
		        "os.arch",
		};
	    
        for (String key : vendorKeys ) {
            String property = System.getProperty(key);
            if (property != null) {
                int code = (property.indexOf("64") >= 0) ? 64 : 32;
                return code;
            }
        }
        return 32;
	}
	
	
    /**
     * Initialize the singleton instance by specifying were GaviotaTablebases are.
     *
     * @param GaviotaTbPath the directory on the file system which contains Gaviota Tablebases files
     * @return true, if successful
     */
    public final boolean init(String GaviotaTbPath) {
    	return init(GaviotaTbPath, 4);
    }
    
    
    /**
     * Initialize the singleton instance by specifying were GaviotaTablebases are,
     * as well as how much memory the cache will use (in megabytes)
     *
     * @param GaviotaTbPath the directory on the file system which contains Gaviota Tablebases files
     * @param GaviotaTbCache the size of cache in megabytes
     * @return true, if successful
     */
    public final native boolean init(String GaviotaTbPath, int GaviotaTbCache);
    
    
    /**
     * Calls the native 'hard probe' function
     * 
     * @param colour_to_move the colour_to_move
     * @param castles the castles
     * @param enpass_sq the enpass_sq
     * @param white_sqs the white_sqs terminated with NATIVE_SQUARE_NONE
     * @param black_sqs the black_sqs terminated with NATIVE_SQUARE_NONE
     * @param white_pids the white_pids terminated with NATIVE_PID_NONE
     * @param black_pids the black_pids terminated with NATIVE_PID_NONE
     * @param result the output in the format: int [2] {NATIVE_INFO_*; plies to mate, if any or zero}
     * 
     * @return true if probing founds record in the table bases.
     */
    public final native boolean probeHard(int colour_to_move,
    									int castles,
    									int enpass_sq,
    									int[] white_sqs, //terminated with NATIVE_SQUARE_NONE
    									int[] black_sqs, //terminated with NATIVE_SQUARE_NONE
    									byte[] white_pids, //terminated with NATIVE_PID_NONE
    									byte[] black_pids, //terminated with NATIVE_PID_NONE
    									int[] result); //output: int [2] {NATIVE_INFO_*; plies to mate, if any or zero}
    
    
    /**
     * Calls the overloaded method by setting 'castles' argument to 0.
     * This method assumes that there are not castling rights so ensure this fact before calling.
     * 
     * @param colour_to_move the colour_to_move
     * @param castles the castles
     * @param enpass_sq the enpass_sq
     * @param white_sqs the white_sqs terminated with NATIVE_SQUARE_NONE
     * @param black_sqs the black_sqs terminated with NATIVE_SQUARE_NONE
     * @param white_pids the white_pids terminated with NATIVE_PID_NONE
     * @param black_pids the black_pids terminated with NATIVE_PID_NONE
     * @param result the output in the format: int [2] {NATIVE_INFO_*; plies to mate, if any or zero}
     * 
     * @return true if probing founds record in the table bases.
     */
    public final boolean probeHard(int colour_to_move,
    									int enpass_sq,
    									int[] white_sqs,
    									int[] black_sqs,
    									byte[] white_pids,
    									byte[] black_pids,
    									int[] result) {
    	return probeHard(colour_to_move, 0, enpass_sq, white_sqs, black_sqs, white_pids, black_pids, result);
    }
}
