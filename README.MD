## Intelignecja Obliczeniowa 2016

# Opracowanie benchmarku dla lokalizacji robotów mobilnych w budynku

## Dane wejściowe

1. Plik z pomiarami dokonanymi przez robota w formacie csv (lub inny format Excela). Pary kąt - odległość do ściany w mm. Pole widzenia - 240 stopni, 3 odczyty na stopień. Poprawne odległości: 5 cm - 5 m (> 5m - nieskonczoność - zera). Dokładność 5 mm. Niewielkie szumy
2. Plik z rozmieszczeniem ścian w budynku (odcinki, ew. raster)
3. Parametry technicze robota - prędkość, zasięg skanu, dokładność skanów, interwał między skanami

## Dane wyjściowe

1. Pozycje robota w czasie kolejnych pomiarów

## Wymagania

Celem projektu jest przygotowanie rozproszonego benchmarku dla lokalizacji robotów mobilnych w budynku.
Wymagania wobec projektu są następujące:

1. Odczytanie danych z plików wejściowych
2. Wykorzystanie języka Scala oraz biblioteki Akka
3. Możliwość dokonywania obliczeń na Zeusie
4. Możliwość rozproszenie obliczeń na więcej niż jeden węzeł Zeusa
5. Równoważenie obciążenia między węzły obliczeniowe
6. Efektem obliczeń ma być ścieżka przebyta przez robota, tj. dla każdego pomiaru należy przyporządkować informację o pozycji w budynku w której został dokonany. Interesują nas tylko rzeczywiste ścieżki, tzn. robot nie mógł się "teleportować"
7. Na początek ścieżka robota w pustym pomieszczeniu, następnie dodanie przeszkód nieruchomych, później ruchomych; propozycja prostego algorytmu score'owania/funkcji celu

## Algorytm

W celu rozwiązania postawionego problemu zdecydowaliśmy się na algorytm składający się z następujących kroków:

1. Mapa budnynku zostaje równo podzielona między węzły biorące udział w obliczeniach. Przy czym każdy z węzłów posiada informację o całej mapie.
2. Każdy węzeł losuje pozycje w przydzielonym fragmencie, następnie porównuje ją z pozycją startową robota.
3. Jeśli róznica między teoretycznymi odczytami robota z wylosowanej pozycji, a faktycznymi jest poniżej dobranej doświadczalnie odległości następuje próba optymalizacji wylosowanej pozycji. Optymalizacja polega na przemieszczeniu wybranej pozycji o losowe przemieszczenie i jej ponownej ocenie.
4. Jeśli w wyniku optymalizacji otrzymamy pozycję ktorej teoretyczne odczyty są w dobranej doświadczalnie odległości od odczytów faktycznych uznajemy, że znaleźliśmy punkt startowy.
5. Próbujemy odtowrzyć ruch robota, dodając do pozycji startowej wektor o długości drogi przebytej przez robota między skanowaniami, aż znajdziemy kolejną pozycję na trasie. Jeśli się nie uda wracami do punktu nr 2.

W dowolnym momencie obliczeń dwa dowolne węzły mogą skontaktować się ze sobą w celu uśrednienia liczby potencjalnych punktów startowych w celu równoważenia obciążenia.

## Funkcja fitness

- Zagregowane nagrody z prawdopodobieństw dla kolejnych promieni
- Rozkład prawdopodobnieństwa na danym promieniu - trójkątny lub (lepiej) Gaussa
- Niewielka kara za odczyt bliższy niż ściana (możliwa przeszkoda), duża kara za odczyt dalszy niż ściana
- Odczyty poniżej progu dokładności (parametr algorytmu) - wyzerowane
- Niedokładność mapy - prawdopodobieństwo nigdy nie osiąga 1