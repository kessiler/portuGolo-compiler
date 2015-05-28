algoritmo
    declare a, b numérico; c literal;
    z <-- a + b * c + Calcula(a, b);
    se(z <> 0) início
        escreva(z); 
        escreva("Olá Mundo!");
    fim
fim algoritmo

subrotina Calcula(a, b numérico) 
    declare c dánadanão;
    c <-- a * b;
    retorne c;
fim subrotina Calcula2