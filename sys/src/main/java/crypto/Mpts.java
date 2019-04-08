package crypto;

import it.unisa.dia.gas.jpbc.Element;
import org.omg.Messaging.SYNC_WITH_TRANSPORT;

import java.util.ArrayList;

public class Mpts extends BilinearPairing {
    private Element Verify, g, z, P, m, skm;

    public Mpts(Element pVal, Element mVal){
        super();
        P = G1.newElement().getImmutable();
        P = pVal.duplicate().getImmutable();
        m = Zr.newElement().getImmutable();
        m = mVal.duplicate().getImmutable();
    }

    /**
     * sign data
     * @param sk
     * @param pkArr
     * @param sigArr
     * @param threshold
     */
    public void run(Element sk, ArrayList<Element> pkArr, ArrayList<Element> sigArr, int threshold) {
        // If the size of signature array is enough, return
        if(sigArr.size()>=threshold)    return;

        // Check history signatures, if false, clear sigArr and pkArr, restart signatures
        if(!verify(pkArr,sigArr,sigArr.size())){
            sigArr.clear();
            pkArr.clear();
        }

        // calculate Signature
        skm = Zr.newElement().getImmutable();
        skm = m.add(sk.duplicate()).getImmutable();

        Element sig = pairing.pairing(P.duplicate(),P.duplicate()).getImmutable();
        if(sigArr.size()==0){
            sig = sig.powZn(skm.duplicate()).getImmutable();
        }else{
            sig = sig.powZn(skm.duplicate()).getImmutable();
            sig = sig.mul(sigArr.get(sigArr.size()-1)).getImmutable();
        }
        // calculate pk
        Element pk = P.duplicate().mulZn(sk.duplicate());

        // add sig and pk to arrays
        sigArr.add(sig);
        pkArr.add(pk);
    }

    /**
     * verify previous signatures
     * @param pkArr
     * @param sigArr
     * @param threshold
     * @return
     */
    public boolean verify(ArrayList<Element> pkArr, ArrayList<Element> sigArr, int threshold) {
        if(sigArr.size()==0)    return true;
        if(sigArr.size()<threshold){
            System.out.println("size < threshold.");
            return false;
        }

        // Verify
        g = G1.newElement().getImmutable();
        z = Zr.newElement().getImmutable();
        z = m.duplicate().getImmutable();
        Verify = GT.newElement().getImmutable();

        for(int i=1;i<pkArr.size();i++){
            z = z.add(m.duplicate()).getImmutable();
        }
        g = P.mulZn(z.duplicate()).getImmutable();
        for(Element pk:pkArr){
            g = g.add(pk.duplicate()).getImmutable();
        }

        Verify = pairing.pairing(g.duplicate(),P.duplicate()).getImmutable();
        if(Verify.isEqual(sigArr.get(sigArr.size()-1))) {
            return true;
        } else {
            return false;
        }
    }
}
