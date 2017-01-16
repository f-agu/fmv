/usr/bin/ffmpeg -y -i {$VIDEO_SOURCE} -nostats -vf scale='trunc(oh*a*sar/2)*2:min(480\,trunc(ih/2)*2)' -vcodec libx264 -preset medium -profile:v baseline -level 3.0 -crf 24 -f mp4 {$VIDEO_DESTINATION}



http://manpages.ubuntu.com/manpages/raring/en/man1/ffmpeg.1.html

ffmpeg -i "2014-04-14 - Au rocher de vierge.mp4" -vsync 0 -vf "select='not(mod(n,104))'" thumbs/out%d.jpg


====================== ffprobe ============================== 

ffprobe -print_format json -show_format -show_streams -v quiet -i VID_20140529_132942.mp4



====================== concat ============================== 
ok :
ffmpeg -i 01.mp4 -i 02.mp4 -filter_complex "[0:0] [0:1] [1:0] [1:1] concat=n=2:v=1:a=1 [v] [a]" -map "[v]" -map "[a]" out.mp4
ffmpeg -i 07.mp4 -i 08.mp4 -i 09.mp4 -filter_complex "[0:0] [0:1] [1:0] [1:1] [2:0] [2:1] concat=n=3:v=1:a=1 [v] [a]" -map "[v]" -map "[a]" out3.mp4

fail :
ffmpeg -i 01.mp4 -i 02.mp4 -i 03.mp4 -i 04.mp4 -filter_complex "[0:0] [0:1] [1:0] [1:1] [2:0] [2:1] [3:0] [3:1] concat=n=4:v=2:a=2 [v] [a]" -map "[v]" -map "[a]" out.mp4

https://ffmpeg.org/ffmpeg-filters.html#Examples-70


====================== speed ============================== 
PTS=N/(FRAME_RATE*TB)

ffmpeg -i in.mp4 -vf "setpts='N/(FRAME_RATE*TB)'" -y out.mp4



-filter_complex "[0:v]setpts=0.125*PTS[v];[0:a]atempo=2.0,atempo=2.0,atempo=2.0[a]"



====================== geoloc ============================== 

+48.8727+002.3491+032.330/

====================== showspectrum ============================== 

https://www.ffmpeg.org/ffmpeg-filters.html#showspectrum
