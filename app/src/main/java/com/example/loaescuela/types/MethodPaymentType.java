package com.example.loaescuela.types;

public enum MethodPaymentType {

        TARJETA(Constants.PAYMENT_TARJETA),
        EFECTIVO(Constants.PAYMENT_EFECTIVO),
        MERCADOPAGO(Constants.PAYMENT_MP),
        TRANSFERENCIA(Constants.PAYMENT_TRANSFERENCIA);

        private final String name;

        MethodPaymentType(String name){
            this.name = name;
        }

        public String getName(){
            return this.name;
        }


}
