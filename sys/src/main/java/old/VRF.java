package old;

import consensus.BilinearPairing;
import it.unisa.dia.gas.jpbc.Element;


public class VRF extends BilinearPairing {

    private Element sk, m, skm, pk, Hash, Proof, VerifyHash, VerifyProof;

    public VRF(){
        super();
        buildSystem();
        encryption();
    }

    public Element getPk() {
        return pk;
    }

    public Element getHash() {
        return Hash;
    }

    public Element getProof() {
        return Proof;
    }

    public void buildSystem() {
        //初始化Zr中的元素
        sk = Zr.newRandomElement().getImmutable();          //随机生成主密钥sk
        m = Zr.newRandomElement().getImmutable();
        skm = Zr.newElement();
        skm = m.add(sk).getImmutable();

        //初始化为G1中的元素，G1是加法群
        pk = G1.newElement();
        pk = P.mulZn(sk).getImmutable();                    // 计算pk=sk*P,注意顺序
        Proof = G1.newElement();
        VerifyProof = G1.newElement();

        //初始化GT中的元素，GT是乘法群
        Hash = GT.newElement();
        VerifyHash = GT.newElement();
    }

    public void encryption(){
        calHash();
        calProof();
    }

    protected void calHash() {
        Hash = pairing.pairing(P, P).getImmutable();        // 计算e（P,P）
        Hash = Hash.powZn(skm).getImmutable();
        System.out.println("VRF_Hash=e(P,P)^(sk+m)=" + Hash);
    }

    protected void calProof() {
        Proof = P.mulZn(skm).getImmutable();
        System.out.println("VRF_Proof=(sk+m)*P=" + Proof);
    }


    public boolean verify() {
        System.out.println("-------------------VRF Verify----------------------");
        VerifyHash = pairing.pairing(P, Proof).getImmutable();
        VerifyProof = P.mulZn(m).getImmutable();
        VerifyProof = pk.add(VerifyProof).getImmutable();
        System.out.println("V1=" + VerifyHash);
        System.out.println("V2=" + VerifyProof);

        if (VerifyHash.isEqual(Hash) && VerifyProof.isEqual(Proof)){
            System.out.println("VRF Verify Success!");
            return true;
        }else{
            System.out.println("VRF Verify Fail!");
            return false;
        }
    }
}
