FROM continuumio/miniconda3:4.9.2-alpine

COPY environment.yml ./

RUN conda env create -f environment.yml

COPY stdlib/ stdlib/
RUN conda run -n runtime conda develop stdlib/python

COPY runtime runtime/runtime/
RUN conda run -n runtime conda develop runtime

ENTRYPOINT conda run -n runtime python -m runtime
EXPOSE 6789
