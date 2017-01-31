## Inteligencja Obliczeniowa 2016

# Opracowanie benchmarku dla lokalizacji robotów mobilnych w budynku

## Dane wejściowe (data/)

1. Plik z pomiarami dokonanymi przez robota w formacie CSV (pary kąt - odległość do ściany w mm). Pole widzenia - 240 stopni, 3 odczyty na stopień. Poprawne odległości: 5 cm - 5 m (> 5m - nieskonczoność - zera). Dokładność 5 mm. Niewielkie szumy.
2. Plik z rozmieszczeniem ścian w budynku (odcinki - ROSON).
3. Parametry technicze robota - prędkość, zasięg skanu, dokładność skanów, interwał między skanami.

## Dane wyjściowe (results/)

1. Plik CSV z wynikowymi pozycjami dla odpowiadajcych pomiarów z pliku wejściowego. Dodatkowo dla każdego pomiaru zamieszczona jest osiągnieta wartość funkcji fitness oraz kolejny punkt i odległość wynikająca z modelu ruchu.
2. Mapa z narysowaną wynikową ścieżką robota. Mapy dla wszystkich punktów znajdują się w [pliku ZIP](results/IO2016_kzieba_pgoralczyk.zip). Przykład - 100 początkowych pozycji robota (dane o przejeździe - [DaneLabirynt1.csv](data/DaneLabirynt1.csv)):

![alt text](https://github.com/xzc1993/IntObl2016/blob/master/map_1_0.png "100 pierwszych punktów dla DaneLabirynt1.csv")

## Algorytm

W celu rozwiązania postawionego problemu zdecydowaliśmy się na algorytm składający się z następujących kroków:

1. Punkty na trasie robota zostają równopodzielone między węzły obliczeniowe.
2. Każdy węzeł dla danego pomiaru dokonuje ustaloną ilość losowań pozycji.
3. Dla każdego losowania jest obliczana wartość funkcji fitness.
4. Najlepsze wylosowane punkty poddawane są symulowanemu wyżarzaniu
5. Punkt o najlepszej wartości funkcji fitness jest wybierany jako pozycja robota w danym kroku.
6. Obliczana jest różnica pozycji wynikająca z modelu ruchu robota i dodawana do rezultatów.
7. Węzły obliczeniowe zapisują swoje wyniki do plików.
8. Pliki są integrowane w jeden wynikowy przez dodatkowy skrypt.

## Funkcja fitness

- Bazuje na błędzie średniokwadratowym ze wszystkich skanów pomiędzy odczytem danym a hipotetycznym dla pozycji.
- Odczyty poniżej/powyżej progu dokładności (parametr algorytmu) nie są brane pod uwagę.

## Szczegóły rozwiązania

1. Wykorzystano języka Scala oraz bibliotekę Akka.
2. Obliczenia były przeprowadzane na klastrze obliczeniowym Zeus (ACK Cyfronet AGH) - równolegle na wielu węzłach - dzięki odpowiednim skryptom.
3. Osobny program rysuje do pliku PNG, na podstawie rezultatów z pliku, mapę pomieszczenia oraz ścieżkę robota z jego rotacją, wartością funkcji fitness oraz różnicą między wybraną pozycją a modelem teoretycznym.
