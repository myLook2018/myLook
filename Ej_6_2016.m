clear;

RGB = imread('PaisajeRGB.png');
I_sin_ecualizar  = rgb2gray(RGB);

[f,c] = size(I_sin_ecualizar);
tamano = f * c;
histograma = imhist(I_sin_ecualizar) / tamano;
acumulador = zeros(size(histograma));
acumulador(1) = histograma(1);
for i = 2 : size(acumulador)
    acumulador(i) = acumulador(i-1) + histograma(i);
end

NivelesGrises = size(histograma);
NuevasIntensidades = acumulador * (NivelesGrises(1,1) - 1);

I_ecualizada = zeros(f,c);
for i=1:f
    for j=1:c
        aux = I_sin_ecualizar(i,j);
        I_ecualizada(i,j) = NuevasIntensidades(aux+1);
    end
end

I_ecualizada = uint8(I_ecualizada);

histograma_ecualizado = imhist(I_ecualizada) / tamano;

figure(1);
subplot(2,1,1)
plot(histograma)
subplot(2,1,2)
plot(histograma_ecualizado)

figure(2);
subplot(2,1,1)
imshow(I_sin_ecualizar)
subplot(2,1,2)
imshow(I_ecualizada)

