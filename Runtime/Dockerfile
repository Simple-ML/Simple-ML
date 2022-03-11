FROM continuumio/miniconda3:4.10.3-alpine

WORKDIR /app/

COPY environment.yml /app/
RUN conda env create -f environment.yml

COPY runtime/ /app/runtime/
RUN conda run -n runtime conda develop runtime

COPY stdlib/ /app/stdlib/
RUN conda run -n runtime conda develop stdlib/python

ENTRYPOINT conda run -n runtime python -m runtime
EXPOSE 6789
