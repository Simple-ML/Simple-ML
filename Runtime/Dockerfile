FROM continuumio/miniconda3:4.9.2-alpine

COPY environment.yml ./

RUN conda env create -f environment.yml

COPY stdlib/python/simpleml simpleml
COPY runtime runtime
RUN conda run -n runtime conda develop .

ENTRYPOINT conda run -n runtime python -m runtime
EXPOSE 6789