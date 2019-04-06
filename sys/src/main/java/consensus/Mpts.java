package consensus;

import it.unisa.dia.gas.jpbc.Element;
import java.util.ArrayList;

public class Mpts extends BilinearPairing{

    private ArrayList<Element> skArr;
    private ArrayList<Element> skmArr;
    private ArrayList<Element> pkArr;
    private ArrayList<Element> sigArr;
    private Element Verify, g, z;
    private int threshold;

    public ArrayList<Element> getPkArr() {
        return pkArr;
    }

    public ArrayList<Element> getSigArr() {
        return sigArr;
    }

    public Mpts(int thresholdi){
        super();
        threshold = thresholdi;
        skArr = new ArrayList<Element>(thresholdi);
        skmArr = new ArrayList<Element>(thresholdi);
        pkArr = new ArrayList<Element>(thresholdi);
        sigArr = new ArrayList<Element>(thresholdi);
        buildSystem();
    }

    /**
     * 初始化
     */
    public void buildSystem() {
        g = G1.newElement().getImmutable();
        z = Zr.newElement().getImmutable();
        z = m.duplicate();
        Verify = GT.newElement().getImmutable();

        for(int i=0;i<threshold;i++){
            Element sk = Zr.newRandomElement().getImmutable();      // 随机生成主密钥sk1
            skArr.add(sk);

            Element skm = Zr.newElement().getImmutable();
            skm = m.add(sk);
            skmArr.add(skm);

            Element pk = G1.newElement().getImmutable();
            pk = P.mulZn(sk);                                       // 计算pk=sk*G,注意顺序
            pkArr.add(pk);
        }
        for(int i=0;i<threshold;i++){
            Element sig = GT.newElement().getImmutable();
            sigArr.add(sig);
        }
    }


    public void encryption() {
        System.out.println("-------------------Mpts Run----------------------");
        for(int i=0;i<threshold;i++){
            calSig(i);
            System.out.println("sig" + (i+1) + "=" + sigArr.get(i));
        }
    }

    protected void calSig(int i) {
        Element sig = pairing.pairing(P,P).getImmutable();
        if(i==0){
            sig = sig.powZn(skmArr.get(i));
        }else{
            sig = sig.powZn(skmArr.get(i));
            sig = sig.mul(sigArr.get(i-1));
        }
        sigArr.set(i,sig);
    }

    public boolean verify(ArrayList<Element> pkArr, ArrayList<Element> sigArr) {
        System.out.println("-------------------Mpts Verify----------------------");
        for(int i=1;i<pkArr.size();i++){
            z = z.add(m);
        }
        g = P.mulZn(z);
        for(Element pk:pkArr){
            g = g.add(pk);
        }
        Verify = pairing.pairing(g,P);
        System.out.println("Verify=" + Verify);

        if(Verify.isEqual(sigArr.get(threshold-1))) {
            System.out.println("Mpts Verify Success!");
            return true;
        } else {
            System.out.println("Mpts Verify Fail!");
            return false;
        }
    }

    private void test(){
    //public static void main(String[] args){
        Mpts mpts = new Mpts(2);
        mpts.encryption();
        mpts.verify(mpts.pkArr,mpts.sigArr);
    }
}
