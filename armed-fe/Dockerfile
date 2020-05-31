FROM httpd:2.4


COPY conf/httpd.conf /usr/local/apache2/conf/httpd.conf
COPY conf/.htaccess /usr/local/apache2/htdocs

EXPOSE 80
EXPOSE 443

ADD dist /usr/local/apache2/htdocs/