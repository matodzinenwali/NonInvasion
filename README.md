# WildTrack – Non‑Invasive Wildlife Monitoring System

A desktop application for South African ecologists that uses graph‑based data structures to identify individual animals from images and compute optimal drone flight paths. Built entirely from scratch in Java with JavaFX – no external libraries, no network communication, no APIs.

## Project Overview

WildTrack addresses two key challenges in wildlife conservation:

1. **Individual Animal Identification**  
   - Recognises specific animals (rhinos, leopards, etc.) from photographs.  
   - Matches unique physical features – coat patterns, ear notches, scars – using custom similarity algorithms.  
   - Completely non‑invasive: no tracking devices, tags, or collars required.

2. **Drone Path Optimisation**  
   - Simulates battery‑efficient flight routes across terrain images.  
   - Uses graph‑based shortest‑path algorithms to cover more ground with less energy.  
   - Demonstrates how autonomous drones can support conservation without disturbing wildlife.

All processing is performed locally on the user's machine, ensuring data privacy and avoiding reliance on internet connectivity in remote field locations.

## Project Theme

This project implements the theme of **Emergent Graph Structures for Image Similarity, Classification, and Path Finding**. Graphs are used to:

- Model spatial relationships between pixels and regions in images.
- Enable similarity searches across collections of animal photographs.
- Compute optimal routes for drone navigation using Dijkstra's algorithm.

## Technology Stack

- **Java 21** – Core programming language.
- **JavaFX 21** – Desktop GUI framework.
- **Custom Data Structures** – All graph, tree, heap, queue, and dictionary implementations are written from scratch (no `java.util.*` collections used).

## Features

- Load and display animal images from local directories.
- Extract feature vectors (colour histograms, edge orientation, down‑sampled pixel grids).
- Index known individuals using a KD‑Tree for fast nearest‑neighbour search.
- Query an unknown animal photo and return the top‑K best matches.
- View similarity scores for matching results.
- Convert any image into a weighted graph (pixels or regions as nodes).
- Compute and visualise the shortest path between two selected points on an image.
- Simulate drone route planning using region‑based graph abstraction.


