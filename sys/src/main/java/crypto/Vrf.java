package crypto;

import it.unisa.dia.gas.jpbc.Element;


public class Vrf extends BilinearPairing {

    private Element skm, Hash, Proof, VerifyHash, VerifyProof, P, m;

    public Vrf(Element pVal, Element mVal){
        super();
        P = G1.newElement().getImmutable();
        P = pVal.duplicate().getImmutable();
        m = Zr.newElement().getImmutable();
        m = mVal.duplicate().getImmutable();
    }

    public Element getHash() {
        return Hash;
    }

    public Element getProof() {
        return Proof;
    }

    /**
     * run vrf, calculate vrf_hash and vrf_proof
     * @param sk
     */
    public void run(Element sk){
        skm = Zr.newElement().getImmutable();
        skm = m.add(sk.duplicate()).getImmutable();
        Proof = G1.newElement().getImmutable();
        Hash = GT.newElement().getImmutable();

        // calculate vrf hash
        Element G = G1.newElement(P.duplicate()).getImmutable();
        Hash = pairing.pairing(G, G).getImmutable(); // 计算e（P,P）
        Hash = Hash.powZn(skm.duplicate()).getImmutable();

        // calculate vrf proof
        Proof = P.duplicate().mulZn(skm.duplicate()).getImmutable();

    }

    /**
     * check vrf
     * @param vrfHash
     * @param vrfProof
     * @param pk
     * @return
     */
    public boolean verify(Element vrfHash, Element vrfProof, Element pk) {
        VerifyProof = G1.newElement().getImmutable();
        VerifyHash = GT.newElement().getImmutable();

        VerifyHash = pairing.pairing(P.duplicate(), vrfProof.duplicate());
        VerifyProof = P.duplicate().mulZn(m).getImmutable();
        VerifyProof = pk.duplicate().add(VerifyProof.duplicate()).getImmutable();

        /*
        System.out.println("pk=" + pk);
        System.out.println("P=" + P);
        System.out.println("m=" + m);
        System.out.println("VerifyHash=" + VerifyHash);
        System.out.println("vrfHash=" + vrfHash);
        System.out.println("VRF_Proof=" + VerifyProof);
        System.out.println("vrfProof=" + vrfProof);
        */

        if (VerifyHash.isEqual(vrfHash) && VerifyProof.isEqual(vrfProof)){
            System.out.println("VRF Verify Success!");
            return true;
        }else{
            System.out.println("VRF Verify Fail!");
            return false;
        }
    }
}
