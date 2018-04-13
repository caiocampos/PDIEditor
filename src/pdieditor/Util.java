/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package pdieditor;

import info.wurzinger.jaiOp.hwst.*;
import info.wurzinger.segmenting.elements.segment.*;
import java.awt.*;
import java.awt.image.*;
import static java.awt.image.ImageObserver.*;
import java.awt.image.renderable.*;
import java.io.*;
import javax.media.jai.*;
import javax.media.jai.operator.*;
import javax.swing.*;
import org.jfree.chart.*;
import org.jfree.chart.plot.*;
import org.jfree.data.category.*;

/**
 *
 * @author Caio
 */
public class Util {
    public static PlanarImage open(String endereco) {
        Image image = new ImageIcon(endereco).getImage();
        if(endereco.endsWith("bmp")) {
            BufferedImage bi;
            try {
                bi = javax.imageio.ImageIO.read(new File(endereco));
            } catch (IOException e) {
                return null;
            }
            return bufferedImageToPlanarImage(bi);
        } else if(image.getWidth(null) != -1 || endereco.endsWith("tif")){
            return JAI.create("fileload", endereco).createInstance();
        } else {
            return null;
        }
    }
    
    public static PlanarImage resize(PlanarImage pi, int zoom) {
        if(pi == null) return null;
        ParameterBlock pb = new ParameterBlock();
        pb.addSource(pi);
        pb.add((float)zoom / 100.0f);
        pb.add((float)zoom / 100.0f);
        pb.add(0.0f);
        pb.add(0.0f);
        pb.add(new InterpolationBicubic2(SOMEBITS));
        return JAI.create("scale", pb).createInstance();
    }
    
    public static PlanarImage grayscale(PlanarImage pi) {
        if(pi == null) return null;
        double[][] matrix;
        if(pi.getSampleModel().getNumBands() == 3) {
            double[][] matrix2 = {{0.3D, 0.59D, 0.11D, 0D}};
            matrix = matrix2;
        } else if(pi.getSampleModel().getNumBands() == 4) {
            double[][] matrix2 = {{0.3D, 0.59D, 0.11D, 0D, 0D}};
            matrix = matrix2;
        } else if(pi.getSampleModel().getNumBands() == 1) {
            double[][] matrix2 = {{1D, 0D}};
            matrix = matrix2;
        } else {
            return null;
        }
        ParameterBlock pb = new ParameterBlock();
        pb.addSource(pi);
        pb.add(matrix);
        return JAI.create("bandcombine", pb).createInstance();
    }
    
    public static PlanarImage toRGB(PlanarImage pi) {
        if(pi == null) return null;
        if(pi.getSampleModel().getNumBands() == 3) {
            return pi;
        } else if(pi.getSampleModel().getNumBands() == 1) {
            return JAI.create("bandselect", pi, new int[]{0, 0, 0}).createInstance();
        }
        return JAI.create("bandselect", pi, new int[]{0, 1, 2}).createInstance();
    }
    
    public static PlanarImage gauss(PlanarImage pi, int t, double d) {
        if(pi == null) return null;
        int ks = 1 + 2 * t;
        int p;
        float[] kernel = new float[ks*ks];
        for(int i = 0; i < ks; i++) {
            for(int j = 0; j < ks; j++) {
                kernel[i*ks+j] = (float)(Math.pow(Math.E, -(((Math.pow(j, 2) + Math.pow(i, 2)))) / (2 * Math.pow(d, 2))) / (2 * Math.PI * Math.pow(d, 2)));
            }
        }
        KernelJAI kerneljai = new KernelJAI(ks, ks, kernel);
        return JAI.create("convolve", pi, kerneljai).createInstance();
    }
    
    public static PlanarImage media(PlanarImage pi, int t) {
        if(pi == null) return null;
        int ks = 1 + 2 * t;
        float[] kernel = new float[ks*ks];
        for(int i = 0; i < kernel.length; i++)
            kernel[i] = 1.0f / (ks * ks);
        KernelJAI kerneljai = new KernelJAI(ks, ks, kernel);
        return JAI.create("convolve", pi, kerneljai).createInstance();
    }
    
    public static PlanarImage mediana(PlanarImage pi, int t) {
        if(pi == null) return null;
        int ks = 1 + 2 * t;
        ParameterBlock pb = new ParameterBlock();
        pb.addSource(pi);
        pb.add(MedianFilterDescriptor.MEDIAN_MASK_SQUARE);
        pb.add(ks);
        return JAI.create("MedianFilter", pb).createInstance();
    }
    
    public static PlanarImage laplace(PlanarImage pi) {
        if(pi == null) return null;
        float[] kernel = {0, -1, 0, -1, 4, -1, 0, -1, 0};
        KernelJAI kerneljai = new KernelJAI(3, 3, kernel);
        return JAI.create("convolve", pi, kerneljai).createInstance();
    }
    
    public static Histogram histograma(PlanarImage pi) {
        ParameterBlock pb = new ParameterBlock();
        int[] bins = {256};
        double[] low = {0.0D};
        double[] high = {255.0D};
        pb.addSource(grayscale(pi));
        pb.add(null);
        pb.add(1);
        pb.add(1);
        pb.add(bins);
        pb.add(low);
        pb.add(high);
        RenderedOp op = JAI.create("histogram", pb, null);
        return (Histogram) op.getProperty("histogram");
    }
    
    public static Histogram histogramaCor(PlanarImage pi) {
        ParameterBlock pb = new ParameterBlock();
        int[] bins = {256, 256, 256};
        double[] low = {0.0D, 0.0D, 0.0D};
        double[] high = {255.0D, 255.0D, 255.0D};
        pb.addSource(toRGB(pi));
        pb.add(null);
        pb.add(1);
        pb.add(1);
        pb.add(bins);
        pb.add(low);
        pb.add(high);
        RenderedOp op = JAI.create("histogram", pb, null);
        return (Histogram) op.getProperty("histogram");
    }
    
    public static PlanarImage gerarGrafico(Histogram h) {
        int[] aux = h.getBins(0);
        DefaultCategoryDataset set = new DefaultCategoryDataset();
        for(int i = 0; i < aux.length; i++) {
            String s = new Integer(8 * (int)(i / 8)).toString();
            set.addValue(aux[i], "Valores", s);
        }
        JFreeChart chart = ChartFactory.createBarChart("Histograma", "Níveis", "Quantidade", set, PlotOrientation.VERTICAL, false, false, false);  
        chart.setBorderVisible(true);  
        chart.setBorderPaint(Color.black);  
        BufferedImage buf = chart.createBufferedImage(1500, 750);
        return bufferedImageToPlanarImage(buf);
    }
    
    private static PlanarImage bufferedImageToPlanarImage(BufferedImage bi) {
        ParameterBlockJAI pb = new ParameterBlockJAI("AWTImage");
        pb.setParameter("AWTImage", bi);
        return JAI.create("AWTImage", pb).createInstance();
    }
    
    public static PlanarImage watershed(PlanarImage pi) {
        if(pi == null) return null;
        int t = Math.max(pi.getHeight(), pi.getWidth());
        double v, iv;
        if(t > 200) {
            v = 100000d / (double)t;
        } else {
            v = 100d;
        }
        iv = 10000d / v;
        PlanarImage ri = toRGB(resize(pi, (int)v));
        WatershedTranformDescriptor.register();
        ParameterBlock pb = new ParameterBlock();
        pb.addSource(ri);
        RenderedOp sOp  = JAI.create("HierarchicalWatershedTranform", pb);
        MergedSegmentMap map = (MergedSegmentMap) sOp.getProperty("segmentMap");
        BufferedImage buf = map.getMosaicImage();
        PlanarImage res = bufferedImageToPlanarImage(buf);
        return resize(res, (int)iv);
    }

    public static PlanarImage segmentar(PlanarImage pi, int cor) {
        BufferedImage bi = toRGB(pi).getAsBufferedImage();
        BufferedImage si = new BufferedImage(pi.getWidth(), pi.getHeight(), BufferedImage.TYPE_INT_RGB);
        for(int y = 0; y < pi.getHeight(); y++) {
            for(int x = 0; x < pi.getWidth(); x++) {
                Color p = new Color(bi.getRGB(x, y));
                double v = p.getRed() * 0.3 + p.getGreen() * 0.59 + p.getBlue() * 0.11;
                int col = v > cor ? 255 : 0;
                int c = (col << 16) | (col << 8) | col;
                si.setRGB(x, y, c);
            }
        }
        PlanarImage fi = bufferedImageToPlanarImage(si);
        return grayscale(fi);
    }
}
