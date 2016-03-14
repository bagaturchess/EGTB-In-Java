#include "bagaturchess_egtb_gaviota_EGTBProbing.h"
#include "gtb-probe.h"
#include <algorithm>

using namespace std;

static bool isInitialized = false;
static bool initOk = false;
static const char** paths = NULL;


jboolean
JNICALL Java_bagaturchess_egtb_gaviota_EGTBProbing_init(
	JNIEnv* env, jobject ths, jstring jtb_path, jint csize)
{
    initOk = false;
    const char* tbPath = (*env).GetStringUTFChars(jtb_path, NULL);
    if (!tbPath)
        return false;

    if (isInitialized && paths)
        tbpaths_done(paths);
    paths = tbpaths_init();
    if (paths == NULL) {
        (*env).ReleaseStringUTFChars(jtb_path, tbPath);
        return false;
    }
    paths = tbpaths_add(paths, tbPath);
    if (paths == NULL) {
        (*env).ReleaseStringUTFChars(jtb_path, tbPath);
        return false;
    }

    TB_compression_scheme scheme = tb_CP4;
    int verbose = 0;
    int cacheSize = csize*1024*1024;
    int wdlFraction = 8;
    if (isInitialized) {
        tb_restart (verbose, scheme, paths);
        tbcache_restart(cacheSize, wdlFraction);
    } else {
        tb_init(verbose, scheme, paths);
        tbcache_init(cacheSize, wdlFraction);
    }
    isInitialized = true;

    (*env).ReleaseStringUTFChars(jtb_path, tbPath);
    initOk = true;
    return true;
}

JNIEXPORT jboolean
JNICALL Java_bagaturchess_egtb_gaviota_EGTBProbing_probeHard(
	JNIEnv* env, jobject ths,
        jint ctm, jint castles, jint epSq,
        jintArray whiteSquares, jintArray blackSquares,
        jbyteArray whitePieces, jbyteArray blackPieces,
        jintArray result)
{
    if (!initOk)
        return false;
    if ((*env).GetArrayLength(result) < 2)
        return false;

    const int MAXLEN = 17;
    unsigned char wp[MAXLEN];
    unsigned int  ws[MAXLEN];
    unsigned char bp[MAXLEN];
    unsigned int  bs[MAXLEN];

    int len = (*env).GetArrayLength(whiteSquares);
    jint* jiPtr = (*env).GetIntArrayElements(whiteSquares, NULL);
    for (int i = 0; i < min(len, MAXLEN); i++)
            ws[i] = jiPtr[i];
    (*env).ReleaseIntArrayElements(whiteSquares, jiPtr, 0);

    len = (*env).GetArrayLength(blackSquares);
    jiPtr = (*env).GetIntArrayElements(blackSquares, NULL);
    for (int i = 0; i < min(len, MAXLEN); i++)
            bs[i] = jiPtr[i];
    (*env).ReleaseIntArrayElements(blackSquares, jiPtr, 0);

    len = (*env).GetArrayLength(whitePieces);
    jbyte* jcPtr = (*env).GetByteArrayElements(whitePieces, NULL);
    for (int i = 0; i < min(len, MAXLEN); i++)
            wp[i] = jcPtr[i];
    (*env).ReleaseByteArrayElements(whitePieces, jcPtr, 0);
	
    len = (*env).GetArrayLength(blackPieces);
    jcPtr = (*env).GetByteArrayElements(blackPieces, NULL);
    for (int i = 0; i < min(len, MAXLEN); i++)
            bp[i] = jcPtr[i];
    (*env).ReleaseByteArrayElements(blackPieces, jcPtr, 0);
	
    unsigned int tbInfo;
    unsigned int plies;
    int ret = tb_probe_hard(ctm,
                            epSq, castles,
                            ws,
                            bs,
                            wp,
                            bp,
                            &tbInfo, &plies);
    jint res[2];
    res[0] = tbInfo;
    res[1] = plies;

    (*env).SetIntArrayRegion(result, 0, 2, res);
    return ret != 0;
}
