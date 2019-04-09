package crypto;

import it.unisa.dia.gas.jpbc.Element;


public class Vrf extends BilinearPairing {
    private Element m;              //系统轮次
    private Element skm;            //sk与m的和
    private Element Hash;           //VRF哈希
    private Element Proof;          //VRF零知识证明
    private Element VerifyHash;     //验证哈希
    private Element VerifyProof;    //验证零知识证明

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
     * 运行VRF算法, 计算VRF哈希和VRF零知识证明
     * VRF_Hash = e(P,P)^(sk+m)
     * VRF_Proof = (sk+m)*P
     * @param sk
     */
    public void run(Element sk){
        skm = Zr.newElement().getImmutable();
        skm = m.add(sk.duplicate()).getImmutable();
        Proof = G1.newElement().getImmutable();
        Hash = GT.newElement().getImmutable();

        // 计算VRF哈希 
        Hash = pairing.pairing(P.duplicate(), P.duplicate()).getImmutable(); //计算e(P,P)
        Hash = Hash.powZn(skm.duplicate()).getImmutable();                   //计算e(P,P)^(sk+m)

        // 计算VRF零知识证明
        Proof = P.duplicate().mulZn(skm.duplicate()).getImmutable();         //计算(sk+m)*P
    }

    /**
     * VRF校验
     * VerifyHash = e(P,VRF_Proof)
     * VerifyProof = pk+m*P
     * @param vrfHash
     * @param vrfProof
     * @param pk
     * @return
     */
    public boolean verify(Element vrfHash, Element vrfProof, Element pk) {
        VerifyProof = G1.newElement().getImmutable();
        VerifyHash = GT.newElement().getImmutable();

        VerifyHash = pairing.pairing(P.duplicate(), vrfProof.duplicate());          //计算e(P,VRF_Proof)
        VerifyProof = P.duplicate().mulZn(m).getImmutable();                        //计算m*P
        VerifyProof = pk.duplicate().add(VerifyProof.duplicate()).getImmutable();   //计算pk+m*P

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
