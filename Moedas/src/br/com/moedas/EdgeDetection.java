package br.com.moedas;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class EdgeDetection {
    public static void main(String[] args) {
        try {
            // Passo 1: Ler a imagem "ascii.pgm"
            int[][] grayImage = readPGMImage("coins.ascii.pgm");

            int height = grayImage.length;
            int width = grayImage[0].length;

            // Arrays para armazenar as imagens Gx e Gy
            int[][] Gx = new int[height][width];
            int[][] Gy = new int[height][width];

            // Criação das threads para calcular Gx e Gy
            Thread threadGx = new Thread(new GxCalculationTask(grayImage, Gx));
            Thread threadGy = new Thread(new GyCalculationTask(grayImage, Gy));

            // Inicia as threads
            threadGx.start();
            threadGy.start();

            // Aguarda até que ambas as threads tenham terminado
            try {
                threadGx.join();
                threadGy.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            // Cálculo da imagem de saída G a partir de Gx e Gy
            int[][] G = new int[height][width];

            for (int i = 0; i < height; i++) {
                for (int j = 0; j < width; j++) {
                    G[i][j] = Gx[i][j] + Gy[i][j];
                    G[i][j] = saturate(G[i][j]);
                }
            }

            // Salvar a imagem de saída em "output.pgm"
            savePGMImage("output.pgm", G);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Função para ler uma imagem PGM a partir de um arquivo
    public static int[][] readPGMImage(String filename) throws IOException {
        BufferedReader reader = new BufferedReader(new FileReader(filename));

        // Ler o cabeçalho
        System.out.println("Cabeçalho:");
        System.out.println(reader.readLine());  // P2
        System.out.println(reader.readLine());  // Comentário
        String[] dimensions = reader.readLine().split(" ");
        int width = Integer.parseInt(dimensions[0]);
        int height = Integer.parseInt(dimensions[1]);
        int maxValue = Integer.parseInt(reader.readLine().trim());
        System.out.println("Largura: " + width);
        System.out.println("Altura: " + height);
        System.out.println("Valor Máximo: " + maxValue);

        // Aloca o array de imagem com o tamanho correto
        int[][] image = new int[height][width];

     // Ler os valores dos pixels e normalizá-los
        for (int i = 0; i < height; i++) {
            String[] pixelValues = reader.readLine().split("\\s+");
            for (int j = 0; j < width; j++) {
                for (String subValue : pixelValues) {
                    if (!subValue.isEmpty()) {
                        try {
                            int value = Integer.parseInt(subValue);
                            // Normalizar o valor, se necessário
                            int normalizedValue = (int) ((value / (double) maxValue) * 255);
                            image[i][j] = normalizedValue;
                            j++; // Avança para o próximo índice de coluna
                        } catch (NumberFormatException ex) {
                            System.err.println("Erro na conversão de valor: " + subValue);
                        }
                    }
                }
            }
        }


        reader.close();
        return image;
    }

    // Função para salvar a imagem PGM em um arquivo
    public static void savePGMImage(String filename, int[][] image) throws IOException {
        FileWriter writer = new FileWriter(filename);

        // Escrever o cabeçalho
        writer.write("P2\n");
        writer.write(image[0].length + " " + image.length + "\n");
        writer.write("255\n");

        // Escrever os valores dos pixels
        for (int i = 0; i < image.length; i++) {
            for (int j = 0; j < image[0].length; j++) {
                writer.write(image[i][j] + " ");
            }
            writer.write("\n");
        }

        writer.close();
    }
    
    // função para garantir os valores dentro da escala
    public static int saturate(int value) {
        if (value < 0) {
            return 0;
        } else if (value > 255) {
            return 255;
        } else {
            return value;
        }
    }

    // Classe para calcular Gx em uma thread
    static class GxCalculationTask implements Runnable {
        private int[][] grayImage;
        private int[][] Gx;

        public GxCalculationTask(int[][] grayImage, int[][] Gx) {
            this.grayImage = grayImage;
            this.Gx = Gx;
        }

        @Override
        public void run() {
            int height = grayImage.length;
            int width = grayImage[0].length;

            // Loop através de pixels internos para calcular Gx
            for (int i = 1; i < height - 1; i++) {
                for (int j = 1; j < width - 1; j++) {
                    // Cálculo de Gx
                    int gx = grayImage[i + 1][j - 1] + grayImage[i + 1][j] + grayImage[i + 1][j + 1] -
                             grayImage[i - 1][j - 1] - grayImage[i - 1][j] - grayImage[i - 1][j + 1];

                    Gx[i][j] = saturate(gx); // Aplicar a saturação
                }
            }

                 }
             }

    // Classe para calcular Gy em uma thread
    static class GyCalculationTask implements Runnable {
        private int[][] grayImage;
        private int[][] Gy;

        public GyCalculationTask(int[][] grayImage, int[][] Gy) {
            this.grayImage = grayImage;
            this.Gy = Gy;
        }

        @Override
        public void run() {
        	   int height = grayImage.length;
               int width = grayImage[0].length;

               // Loop através de pixels internos para calcular Gy
               for (int i = 1; i < height - 1; i++) {
                   for (int j = 1; j < width - 1; j++) {
                       // Cálculo de Gy
                       int gy = grayImage[i - 1][j + 1] + grayImage[i][j + 1] + grayImage[i + 1][j + 1] -
                                grayImage[i - 1][j - 1] - grayImage[i][j - 1] - grayImage[i + 1][j - 1];
                       Gy[i][j] = saturate(gy);
                }
            }
        }
     }
    
  }