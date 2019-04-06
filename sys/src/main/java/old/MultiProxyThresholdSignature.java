package old;

import consensus.BilinearPairing;
import it.unisa.dia.gas.jpbc.Element;
import java.util.ArrayList;

public class MultiProxyThresholdSignature extends BilinearPairing {

    private ArrayList<Element> skArr;
    private ArrayList<Element> skmArr;
    private ArrayList<Element> pkArr;
    private ArrayList<Element> sigArr;
    private Element m, G, Verify, g, z;
    private int threshold;

    public ArrayList<Element> getPkArr() {
        return pkArr;
    }

    public ArrayList<Element> getSigArr() {
        return sigArr;
    }

    public MultiProxyThresholdSignature(int thresholdi){
        super();
        threshold = thresholdi;
        skArr = new ArrayList<Element>(thresholdi);
        skmArr = new ArrayList<Element>(thresholdi);
        pkArr = new ArrayList<Element>(thresholdi);
        sigArr = new ArrayList<Element>(thresholdi);
        buildSystem();
        encryption();
    }

    /**
     * 初始化
     */
    public void buildSystem() {
        G = G1.newRandomElement().getImmutable();                   // 生成G1的生成元G
        g = G1.newElement();
        m = Zr.newRandomElement().getImmutable();                   // 生成m
        z = m.duplicate().getImmutable();
        Verify = GT.newElement();
        for(int i=0;i<threshold;i++){
            Element sk = Zr.newRandomElement().getImmutable();      // 随机生成主密钥sk1
            skArr.add(sk);

            Element skm = Zr.newElement();
            skm = m.add(sk).getImmutable();
            skmArr.add(skm);

            Element pk = G1.newElement();
            pk = G.mulZn(sk).getImmutable();                        // 计算pk=sk*G,注意顺序
            pkArr.add(pk);
        }
        for(int i=0;i<threshold;i++){
            Element sig = GT.newElement();
            sigArr.add(sig);
        }
    }


    public void encryption() {
        for(int i=0;i<threshold;i++){
            calSig(i);
            System.out.println("sig" + (i+1) + "=" + sigArr.get(i));
        }
    }

    protected void calSig(int i) {
        Element sig = pairing.pairing(G,G).getImmutable();
        if(i==0){
            sig = sig.powZn(skmArr.get(i)).getImmutable();
        }else{
            sig = sig.powZn(skmArr.get(i)).getImmutable();
            sig = sig.mul(sigArr.get(i-1)).getImmutable();
        }
        sigArr.set(i,sig);
    }

    public boolean verify() {
        for(int i=1;i<threshold;i++){
            z = z.add(m).getImmutable();
        }
        g = G.mulZn(z).getImmutable();
        for(Element pk:pkArr){
            g = g.add(pk).getImmutable();
        }
        Verify = pairing.pairing(g,G).getImmutable();
        System.out.println("Verify=" + Verify);

        if(Verify.isEqual(sigArr.get(threshold-1))) {
            System.out.println("Verify Success!");
            return true;
        } else {
            System.out.println("Verify Fail!");
            return false;
        }
    }

    private void test(){
    //public static void main(String[] args){
        MultiProxyThresholdSignature Mpts = new MultiProxyThresholdSignature(2);
        Mpts.verify();
    }
}
