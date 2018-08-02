clear

mf = input('filas del filtro: ');
nf = input('columnas del filtro ');
g = input('Matriz g: ');

[mg, ng] = size(g)

c = zeros((mg - mf + 1),(ng - nf + 1));
c1 = zeros((mg - mf + 1),(ng - nf + 1));


for ic = 1:(mg - mf + 1)
    for jc = 1:(ng - nf + 1)
        for ig = ic: ic + mf - 1
            for jg = jc:jc + nf - 1
                c(ic,jc) = c(ic,jc) + g(ig,jg);
            end
        end
   end
end

g

'Matriz resultado de aplicar filtro del valor medio'  
c = c./(mf * nf)     

for ic = 1:(mg- mf + 1)
    for jc = 1:(ng - nf + 1)
        a = 0;
        for ig = ic:ic + mf - 1
            for jg = jc:jc + nf - 1
                a = a + 1;
                aux(a) = g(ig,jg);
            end
        end
        aux1 = 0;
        for e = 1: a
            aux1 = aux(e);
            for i = e: a
               if aux(i) < aux1
                   aux1 = aux(i);
                   aux(i) = aux(e);
                   aux(e) = aux1;
               end
            end
        end
        
        c1(ic,jc) = aux((nf * mf - 1) / 2 + 1);

    end
end

'Matriz resultado de aplicar filtro de la mediana'  
c1  

figure(1)
image(g)
figure(2)
image(c)
figure(3)
image(c1)




