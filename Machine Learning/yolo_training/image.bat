REM darknet.exe detector test c20/obj.data c20/yolov4-obj.cfg c20/yolov4-obj_best.weights -ext_output -dont_show -out result.json <c20/test_local.txt
darknet.exe detector test c20/obj.data c20/yolov4-obj.cfg c20/yolov4-obj_best.weights -ext_output
REM ./darknet detector test c20/obj.data c20/yolov4-obj.cfg c20/yolov4-obj_best.weights -ext_output -dont_show