package crypto;

import it.unisa.dia.gas.jpbc.Element;
import it.unisa.dia.gas.jpbc.Field;
import it.unisa.dia.gas.jpbc.Pairing;
import it.unisa.dia.gas.plaf.jpbc.pairing.PairingFactory;

public class BilinearPairing {
    public static Pairing pairing;
    public static Field Zr, G1, GT;
    public static Element P, m, sk;
    /**
     * Bilinear Pairing
     */
    public BilinearPairing(){
        /*
        //产生曲线和双线性对
        TypeACurveGenerator pg = new TypeACurveGenerator(256, 256);
        PairingParameters typeAParams = pg.generate();
        //存储到文件中
        Out out = new Out("a.properties");          
        out.println(typeAParams);
        */
        //从文件中读取双线性对
        pairing = PairingFactory.getPairing("a.properties");
        PairingFactory.getInstance().setUsePBCWhenPossible(true);
        Zr = pairing.getZr();           //Zr
        G1 = pairing.getG1();           //G1是加法群
        GT = pairing.getGT();           //GT是乘法群
    }

    public static Field getZr() {
        return Zr;
    }
}
