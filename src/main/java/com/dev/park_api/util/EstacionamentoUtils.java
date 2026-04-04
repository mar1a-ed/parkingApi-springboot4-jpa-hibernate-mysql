package com.dev.park_api.util;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class EstacionamentoUtils {

    private static final double PRIMEIROS_15_MINUTOS = 5.00;

    private static final double PRIMEIROS_60_MINUTOS = 9.25;

    private static final double ADCIONAL_15_MINUTOS = 1.75;

    private static final double DESCONTO_PERCENTUAL = 0.30;

    public static String gerarRecibo(){
        LocalDateTime date = LocalDateTime.now();
        String recibo = date.toString().substring(0, 19);
        return recibo.replace("-", "")
                .replace(":", "")
                .replace("T", "-");
    }

    public static BigDecimal calcularCusto(LocalDateTime entrada, LocalDateTime saida){
        long minutes = entrada.until(saida, ChronoUnit.MINUTES);
        double total = 0.0;

        if(minutes <= 15){
            total = PRIMEIROS_15_MINUTOS;
        }else if(minutes <= 60){
            total = PRIMEIROS_60_MINUTOS;
        }else{
            minutes = minutes - 60;
            long total15MinutosFaixas = (long) Math.ceil(minutes / 15.0);

            total = PRIMEIROS_60_MINUTOS + (ADCIONAL_15_MINUTOS * total15MinutosFaixas);
        }

        return new BigDecimal(total).setScale(2, RoundingMode.HALF_EVEN);
    }

    public static BigDecimal calcularDesconto(BigDecimal custo, long numeroDeVezes){

        BigDecimal desconto = null;

        if(numeroDeVezes >= 10){
            desconto = BigDecimal.valueOf(DESCONTO_PERCENTUAL * 0.30);
        }else{
            desconto = BigDecimal.valueOf(0);
        }

        return desconto.setScale(2, RoundingMode.HALF_EVEN);
    }

}
