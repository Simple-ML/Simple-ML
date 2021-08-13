FROM continuumio/miniconda3:4.9.2-alpine

COPY Runtime/environment.yml ./

RUN conda env create -f environment.yml

COPY Stdlib/python/simpleml simpleml
COPY Runtime/runtime runtime
RUN conda run -n runtime conda develop .

ENTRYPOINT conda run -n runtime python -m runtime
EXPOSE 6789
