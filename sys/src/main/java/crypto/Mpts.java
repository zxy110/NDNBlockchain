package crypto;

import it.unisa.dia.gas.jpbc.Element;
import org.omg.Messaging.SYNC_WITH_TRANSPORT;

import java.util.ArrayList;

public class Mpts extends BilinearPairing {
    private Element Verify;         //验证
    private Element g;              //验证中间变量
    private Element z;              //验证中间变量
    private Element P;              //G1生成元
    private Element m;              //签名消息
    private Element skm;            //sk+m

    public Mpts(Element pVal, Element mVal){
        super();
        P = G1.newElement().getImmutable();
        P = pVal.duplicate().getImmutable();
        m = Zr.newElement().getImmutable();
        m = mVal.duplicate().getImmutable();
    }

    /**
     * 对数据包进行多重门限签名
     * 1. 检查签名个数，若满足门限条件，返回
     * 2. 检查历史签名，若历史签名错误，则清空历史签名，重新签名
     * 3. 计算签名：
            SIG1 = e(P,P)^(sk1+m)
            SIGn=SIG(n-1)*e(P,P)^(skn+m)
     * @param sk
     * @param pkArr
     * @param sigArr
     * @param threshold
     */
    public void run(Element sk, ArrayList<Element> pkArr, ArrayList<Element> sigArr, int threshold) {
        // 若签名个数满足门限条件则返回
        if(sigArr.size()>=threshold)    return;

        // 检查历史签名，若历史签名错误，则清空历史签名，重新签名
        if(!verify(pkArr,sigArr,sigArr.size())){
            sigArr.clear();
            pkArr.clear();
        }

        // 计算签名
        skm = Zr.newElement().getImmutable();
        skm = m.add(sk.duplicate()).getImmutable();    //skm=sk+m                  

        Element sig = pairing.pairing(P.duplicate(),P.duplicate()).getImmutable(); 
        if(sigArr.size()==0){
            sig = sig.powZn(skm.duplicate()).getImmutable();    //SIG1=e(P,P)^(sk1+m)       
        }else{
            sig = sig.powZn(skm.duplicate()).getImmutable();           
            sig = sig.mul(sigArr.get(sigArr.size()-1)).getImmutable();    //SIGn=SIG(n-1)*e(P,P)^(skn+m)
        }
        // 计算公钥
        Element pk = P.duplicate().mulZn(sk.duplicate());    //pk=sk*P        
        // 将签名和公钥加入数组中
        sigArr.add(sig);
        pkArr.add(pk);
    }

    /**
     * 检查签名组
     * 1. 签名组为0的情况
     * 2. 签名个数不满足门限值，返回false
     * 3. 检查签名
            Verify = e(sum(m)*P+sum(pk),P)
     * @param pkArr
     * @param sigArr
     * @param threshold
     * @return
     */
    public boolean verify(ArrayList<Element> pkArr, ArrayList<Element> sigArr, int threshold) {
        //若签名组无数据，返回true
        if(sigArr.size()==0)    return true;
        //签名个数不满足门限值，返回false
        if(sigArr.size()<threshold){
            System.out.println("size < threshold.");
            return false;
        }

        //检查签名
        g = G1.newElement().getImmutable();
        z = Zr.newElement().getImmutable();
        z = m.duplicate().getImmutable();
        Verify = GT.newElement().getImmutable();

        for(int i=1;i<pkArr.size();i++){
            z = z.add(m.duplicate()).getImmutable();    //z=sum(m) 
        }
        g = P.mulZn(z.duplicate()).getImmutable();      
        for(Element pk:pkArr){
            g = g.add(pk.duplicate()).getImmutable();    //g=sum(m)*P+sum(pk)
        }
    
        Verify = pairing.pairing(g.duplicate(),P.duplicate()).getImmutable();    //Verify=e(g,P)=e(sum(m)*P+sum(pk),P)
        if(Verify.isEqual(sigArr.get(sigArr.size()-1))) {
            return true;
        } else {
            return false;
        }
    }
}
