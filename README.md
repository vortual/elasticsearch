# elasticsearch
1.  这是在我们商城基础上开发的，下载完后一些配置要改改，用到的mapper,数据库啥的都是我们商城的，可以直接用
2.  可以先在慕课网看下elasticsearch的入门视频
3.  看完视频后就可以试试运行了，先要安装好elasticsearch
4.  然后是创建索引，创建索引的json文件已经准备好了，在resource文件夹下
5.  创建完索引后，跟solr一样要将数据库数据导入索引库中，步骤如下：
        运行vortualmall-manage-service下test文件夹下的AddIndex类，运行里面的addIndex方法,其中一些配置和mapper的注入要自己搞下
6.  将数据导入索引库后，直接运行taotao-search-service：tomcat7:run跑起来即可
