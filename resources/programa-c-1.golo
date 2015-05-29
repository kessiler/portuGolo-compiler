algoritmo
    declare numérico a, b; literal c;
    z <-- a + b * c + Calcula(a, b);
    se(z <> 0) início
        escreva(z); 
        escreva("Olá Mundo!");
    fim
fim algoritmo

subrotina Calcula(a, b numérico) 
    declare dánadanão c;
    c <-- a * b;
    retorne c
fim subrotina Calcula