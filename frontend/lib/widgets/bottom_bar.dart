import 'package:ale_okaz/consts/colors.dart';
import 'package:flutter/material.dart';
import 'package:get/get.dart';

class BottomBar extends StatelessWidget {
  const BottomBar({super.key});

  @override
  Widget build(BuildContext context) {
    return ClipRRect(
      borderRadius: const BorderRadius.only(
        topLeft: Radius.circular(40),
        topRight: Radius.circular(40),
      ),
      child: BottomAppBar(
          height: 60,
          color: componentColor,
          child: SizedBox(
              child: Row(children: [
            Expanded(
              child: InkWell(
                onTap: () => {Get.toNamed('map')},
                child: const SizedBox.expand(
                    child: Icon(Icons.location_on, color: Colors.green)),
              ),
            ),
            Expanded(
              child: InkWell(
                onTap: () => {Get.toNamed('/take-picture')},
                child: const SizedBox.expand(
                    child: Icon(
                  Icons.photo_camera,
                  color: Colors.green,
                )),
              ),
            ),
            Expanded(
              child: InkWell(
                onTap: () => {Get.toNamed('/profile')},
                child: const SizedBox.expand(
                    child: Icon(Icons.account_circle, color: Colors.green)),
              ),
            )
          ]))),
    );
  }
}
