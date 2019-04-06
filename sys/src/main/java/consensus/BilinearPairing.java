package consensus;

import it.unisa.dia.gas.jpbc.Element;
import it.unisa.dia.gas.jpbc.Field;
import it.unisa.dia.gas.jpbc.Pairing;
import it.unisa.dia.gas.jpbc.PairingParameters;
import it.unisa.dia.gas.plaf.jpbc.pairing.PairingFactory;
import it.unisa.dia.gas.plaf.jpbc.pairing.a.TypeACurveGenerator;


public class BilinearPairing {
    public static Pairing pairing;
    public static Field Zr, G1, GT;
    public static Element P;
    public static Element m;
    /**
     * Bilinear Pairing
     */
    public BilinearPairing(){
        //generate curve and pairing
        TypeACurveGenerator pg = new TypeACurveGenerator(256, 256);
        PairingParameters typeAParams = pg.generate();
        pairing = PairingFactory.getPairing(typeAParams);
        PairingFactory.getInstance().setUsePBCWhenPossible(true);
        Zr = pairing.getZr();
        G1 = pairing.getG1();
        GT = pairing.getGT();
        P = G1.newRandomElement().getImmutable();           // 生成G1的生成元P
        m = Zr.newRandomElement().getImmutable();
    }

    public static Field getG1() {
        return G1;
    }

    public static Field getGT() {
        return GT;
    }

    public static Field getZr() {
        return Zr;
    }

}
